package io.github.dkuechler.longjavaty.users.service;

import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.MeasurementResponse;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutResponse;
import io.github.dkuechler.longjavaty.healthmetrics.repository.MeasurementRepository;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import io.github.dkuechler.longjavaty.users.controller.dto.UserDataExport;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final AppUserRepository appUserRepository;
    private final WorkoutRepository workoutRepository;
    private final MeasurementRepository measurementRepository;

    public UserService(AppUserRepository appUserRepository,
                      WorkoutRepository workoutRepository,
                      MeasurementRepository measurementRepository) {
        this.appUserRepository = appUserRepository;
        this.workoutRepository = workoutRepository;
        this.measurementRepository = measurementRepository;
    }

    @Transactional
    public AppUser getOrCreateUser(UUID id, String email) {
        return appUserRepository.findById(id)
                .orElseGet(() -> {
                    AppUser newUser = new AppUser(email);
                    newUser.setId(id);
                    return appUserRepository.save(newUser);
                });
    }

    /**
     * GDPR Article 20 - Right to Data Portability
     * Exports all user data in a structured, machine-readable format (JSON).
     * 
     * @param userId The UUID of the user requesting data export
     * @return Complete export of all user data
     * @throws NoSuchElementException if user doesn't exist
     */
    @Transactional(readOnly = true)
    public UserDataExport exportUserData(UUID userId) {
        log.info("GDPR data export request for user: {}", userId);
        
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        
        List<WorkoutResponse> workouts = workoutRepository.findByUserIdOrderByStartTimeDesc(userId)
                .stream()
                .map(WorkoutResponse::from)
                .toList();
        
        List<MeasurementResponse> measurements = measurementRepository.findByUserId(userId)
                .stream()
                .map(MeasurementResponse::from)
                .toList();
        
        UserDataExport.UserInfo userInfo = new UserDataExport.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
        
        log.info("Exported {} workouts and {} measurements for user: {}", 
                workouts.size(), measurements.size(), userId);
        
        return new UserDataExport(userInfo, workouts, measurements, OffsetDateTime.now());
    }

    /**
     * GDPR Article 17 - Right to Erasure
     * Deletes user and all associated data (workouts, measurements, heart rate samples).
     * 
     * Cascade deletion is handled by database foreign key constraints with ON DELETE CASCADE.
     * 
     * @param userId The UUID of the user to delete
     * @return true if user was found and deleted, false if user didn't exist
     */
    @Transactional
    public boolean deleteUserAndAllData(UUID userId) {
        log.info("GDPR deletion request for user: {}", userId);
        
        boolean existed = appUserRepository.existsById(userId);
        
        if (existed) {
            // This will cascade delete all related data due to ON DELETE CASCADE constraints
            appUserRepository.deleteById(userId);
            log.info("Successfully deleted user and all associated data: {}", userId);
            return true;
        } else {
            log.warn("Deletion requested for non-existent user: {}", userId);
            return false;
        }
    }
}
