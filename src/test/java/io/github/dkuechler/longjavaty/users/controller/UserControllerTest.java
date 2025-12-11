package io.github.dkuechler.longjavaty.users.controller;

import io.github.dkuechler.longjavaty.AbstractIntegrationTest;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.healthmetrics.repository.MeasurementRepository;
import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Test
    void exportMyData_shouldReturnAllUserData() throws Exception {
        // Given: A user with measurements and workouts
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser("export-test@example.com");
        user.setId(userId);
        appUserRepository.save(user);

        Measurement measurement = new Measurement(user, MeasurementType.HEART_RATE, 72.0,
                OffsetDateTime.now(), "test-source");
        measurementRepository.save(measurement);

        Workout workout = new Workout(user, "CYCLING", "ext-456", OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(2), 7200, 7200, 800.0, 20000.0,
                150, 170, 130, false, "test-source");
        workoutRepository.save(workout);

        // When: User exports their data
        mockMvc.perform(get("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId.toString()))
                .andExpect(jsonPath("$.user.email").value("export-test@example.com"))
                .andExpect(jsonPath("$.workouts").isArray())
                .andExpect(jsonPath("$.workouts[0].workoutType").value("CYCLING"))
                .andExpect(jsonPath("$.measurements").isArray())
                .andExpect(jsonPath("$.measurements[0].measurementType").value("HEART_RATE"))
                .andExpect(jsonPath("$.exportedAt").exists());
    }

    @Test
    void deleteMyAccount_shouldDeleteUserAndAllData() throws Exception {
        // Given: A user with measurements and workouts
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser("test-delete@example.com");
        user.setId(userId);
        appUserRepository.save(user);

        Measurement measurement = new Measurement(user, MeasurementType.RESTING_HEART_RATE, 65.0,
                OffsetDateTime.now(), "test-source");
        measurementRepository.save(measurement);

        Workout workout = new Workout(user, "RUNNING", "ext-123", OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1), 3600, 3600, 500.0, 5000.0,
                140, 160, 120, false, "test-source");
        workoutRepository.save(workout);

        // Verify data exists
        assertThat(appUserRepository.findById(userId)).isPresent();
        assertThat(measurementRepository.findAll()).isNotEmpty();
        assertThat(workoutRepository.findAll()).isNotEmpty();

        // When: User deletes their account
        mockMvc.perform(delete("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                .andExpect(status().isNoContent());

        // Then: User and all data should be deleted
        assertThat(appUserRepository.findById(userId)).isEmpty();
        assertThat(measurementRepository.count()).isZero();
        assertThat(workoutRepository.count()).isZero();
    }

    @Test
    void deleteMyAccount_nonExistentUser_shouldReturn404() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/me/data")
                        .with(jwt().jwt(jwt -> jwt.subject(nonExistentUserId.toString()))))
                .andExpect(status().isNotFound());
    }
}
