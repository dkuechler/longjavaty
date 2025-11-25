package io.github.dkuechler.longjavaty.healthmetrics.service;

import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final AppUserRepository appUserRepository;

    public WorkoutService(WorkoutRepository workoutRepository, AppUserRepository appUserRepository) {
        this.workoutRepository = workoutRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Workout recordWorkout(UUID userId, String workoutType, OffsetDateTime startTime, Integer durationSeconds,
                                 Double caloriesBurned, Double distanceMeters, String sourceId) {
        AppUser user = findUser(userId);
        OffsetDateTime timestamp = startTime != null ? startTime : OffsetDateTime.now();
        Workout workout = new Workout(user, workoutType, timestamp, durationSeconds, caloriesBurned, distanceMeters, sourceId);
        return workoutRepository.save(workout);
    }

    @Transactional(readOnly = true)
    public List<Workout> listWorkouts(UUID userId, OffsetDateTime from, OffsetDateTime to) {
        findUser(userId);
        OffsetDateTime rangeStart = from != null ? from : OffsetDateTime.now().minusYears(100);
        OffsetDateTime rangeEnd = to != null ? to : OffsetDateTime.now();

        if (from != null || to != null) {
            return workoutRepository.findByUserIdAndStartTimeBetween(userId, rangeStart, rangeEnd);
        }
        return workoutRepository.findByUserIdOrderByStartTimeDesc(userId);
    }

    private AppUser findUser(UUID userId) {
        return appUserRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }
}
