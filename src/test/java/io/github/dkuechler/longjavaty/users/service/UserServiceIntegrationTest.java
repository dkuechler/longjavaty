package io.github.dkuechler.longjavaty.users.service;

import io.github.dkuechler.longjavaty.config.TestSecurityConfig;
import io.github.dkuechler.longjavaty.users.controller.dto.UserDataExport;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.repository.MeasurementRepository;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestSecurityConfig.class)
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Test
    void exportUserData_shouldReturnAllUserData() {
        // Given: User with workouts and measurements
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser("export-test@example.com");
        user.setId(userId);
        user = appUserRepository.saveAndFlush(user);

        Workout workout = new Workout(user, "RUNNING", "ext-123",
                OffsetDateTime.now().minusHours(2), OffsetDateTime.now().minusHours(1),
                3600, 3600, 500.0, 5000.0, 140, 160, 120, false, "test-source");
        workoutRepository.saveAndFlush(workout);

        Measurement measurement = new Measurement(user, MeasurementType.HEART_RATE, 72.0,
                OffsetDateTime.now(), "test-source");
        measurementRepository.saveAndFlush(measurement);

        // When: Export user data
        UserDataExport export = userService.exportUserData(user.getId());

        // Then: All data is present
        assertThat(export).isNotNull();
        assertThat(export.user().id()).isEqualTo(user.getId());
        assertThat(export.user().email()).isEqualTo("export-test@example.com");
        assertThat(export.workouts()).hasSize(1);
        assertThat(export.workouts().get(0).workoutType()).isEqualTo("RUNNING");
        assertThat(export.measurements()).hasSize(1);
        assertThat(export.measurements().get(0).measurementType()).isEqualTo(MeasurementType.HEART_RATE);
        assertThat(export.exportedAt()).isNotNull();
    }

    @Test
    void exportUserData_nonExistentUser_shouldThrowException() {
        // Given: Non-existent user ID
        UUID nonExistentId = UUID.randomUUID();

        // When & Then: Should throw exception
        assertThatThrownBy(() -> userService.exportUserData(nonExistentId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deleteUserAndAllData_shouldCascadeDeleteAllRelatedData() {
        // Given: User with workouts, measurements, and heart rate samples
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser("delete-test@example.com");
        user.setId(userId);
        user = appUserRepository.saveAndFlush(user);

        Workout workout = new Workout(user, "CYCLING", "ext-456",
                OffsetDateTime.now().minusHours(3), OffsetDateTime.now().minusHours(1),
                7200, 7200, 800.0, 20000.0, 150, 170, 130, false, "test-source");
        workoutRepository.saveAndFlush(workout);

        Measurement measurement1 = new Measurement(user, MeasurementType.RESTING_HEART_RATE, 65.0,
                OffsetDateTime.now(), "test-source-1");
        Measurement measurement2 = new Measurement(user, MeasurementType.STEPS, 10000.0,
                OffsetDateTime.now(), "test-source-2");
        measurementRepository.saveAndFlush(measurement1);
        measurementRepository.saveAndFlush(measurement2);

        // Verify data exists
        long measurementCountBefore = measurementRepository.count();
        assertThat(measurementCountBefore).isEqualTo(2);

        // When: Delete user and all data
        boolean deleted = userService.deleteUserAndAllData(userId);

        // Then: User and all related data are deleted
        assertThat(deleted).isTrue();
        assertThat(appUserRepository.findById(userId)).isEmpty();
        assertThat(workoutRepository.findByUserIdOrderByStartTimeDesc(userId)).isEmpty();
        // Verify measurements were cascade deleted
        long measurementCountAfter = measurementRepository.count();
        assertThat(measurementCountAfter).isZero();
    }

    @Test
    void deleteUserAndAllData_nonExistentUser_shouldReturnFalse() {
        // Given: Non-existent user ID
        UUID nonExistentId = UUID.randomUUID();

        // When: Try to delete non-existent user
        boolean deleted = userService.deleteUserAndAllData(nonExistentId);

        // Then: Should return false
        assertThat(deleted).isFalse();
    }

    @Test
    void deleteUserAndAllData_shouldNotAffectOtherUsers() {
        // Given: Two users with their own data
        AppUser user1 = new AppUser("user1@example.com");
        user1.setId(UUID.randomUUID());
        user1 = appUserRepository.saveAndFlush(user1);

        AppUser user2 = new AppUser("user2@example.com");
        user2.setId(UUID.randomUUID());
        user2 = appUserRepository.saveAndFlush(user2);

        Workout workout1 = new Workout(user1, "RUNNING", "ext-1",
                OffsetDateTime.now(), OffsetDateTime.now().plusHours(1),
                3600, 3600, 500.0, 5000.0, 140, 160, 120, false, "test-source");
        workoutRepository.saveAndFlush(workout1);

        Workout workout2 = new Workout(user2, "CYCLING", "ext-2",
                OffsetDateTime.now(), OffsetDateTime.now().plusHours(1),
                3600, 3600, 600.0, 10000.0, 150, 170, 130, false, "test-source");
        workoutRepository.saveAndFlush(workout2);

        UUID user1Id = user1.getId();
        UUID user2Id = user2.getId();

        // When: Delete user1
        boolean deleted = userService.deleteUserAndAllData(user1Id);

        // Then: User1 deleted, user2 unaffected
        assertThat(deleted).isTrue();
        assertThat(appUserRepository.findById(user1Id)).isEmpty();
        assertThat(workoutRepository.findByUserIdOrderByStartTimeDesc(user1Id)).isEmpty();

        assertThat(appUserRepository.findById(user2Id)).isPresent();
        assertThat(workoutRepository.findByUserIdOrderByStartTimeDesc(user2Id)).hasSize(1);
    }
}
