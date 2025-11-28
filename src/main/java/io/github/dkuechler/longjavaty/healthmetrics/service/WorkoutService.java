package io.github.dkuechler.longjavaty.healthmetrics.service;

import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.model.WorkoutHeartRateSample;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutHeartRateSampleRepository;
import io.github.dkuechler.longjavaty.healthmetrics.repository.WorkoutRepository;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutHeartRateSamplesRequest;
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
    private final WorkoutHeartRateSampleRepository heartRateSampleRepository;
    private final AppUserRepository appUserRepository;

    public WorkoutService(WorkoutRepository workoutRepository, WorkoutHeartRateSampleRepository heartRateSampleRepository, AppUserRepository appUserRepository) {
        this.workoutRepository = workoutRepository;
        this.heartRateSampleRepository = heartRateSampleRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Workout recordWorkout(UUID userId, String workoutType, String externalId, OffsetDateTime startTime, OffsetDateTime endTime,
                                 Integer durationSeconds, Integer activeDurationSeconds, Double caloriesBurned, Double distanceMeters,
                                 Integer avgHeartRate, Integer maxHeartRate, Integer minHeartRate, Boolean routeAvailable, String sourceId) {
        AppUser user = findUser(userId);
        OffsetDateTime timestamp = startTime != null ? startTime : OffsetDateTime.now();
        OffsetDateTime resolvedEndTime = endTime;
        if (resolvedEndTime == null && durationSeconds != null) {
            resolvedEndTime = timestamp.plusSeconds(durationSeconds);
        }
        Integer resolvedActiveDuration = activeDurationSeconds != null ? activeDurationSeconds : durationSeconds;
        boolean resolvedRouteAvailable = routeAvailable != null && routeAvailable;

        Workout workout = new Workout(
            user,
            workoutType,
            externalId,
            timestamp,
            resolvedEndTime,
            durationSeconds,
            resolvedActiveDuration,
            caloriesBurned,
            distanceMeters,
            avgHeartRate,
            maxHeartRate,
            minHeartRate,
            resolvedRouteAvailable,
            sourceId
        );
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

    @Transactional
    public List<WorkoutHeartRateSample> recordHeartRateSamples(UUID userId, String externalWorkoutId, List<WorkoutHeartRateSamplesRequest.HeartRateSampleDto> samples) {
        Workout workout = findWorkout(userId, externalWorkoutId);
        List<WorkoutHeartRateSample> entities = samples.stream()
            .map(sample -> new WorkoutHeartRateSample(workout, sample.timestamp(), sample.bpm(), sample.sourceId()))
            .toList();
        return heartRateSampleRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public List<WorkoutHeartRateSample> listHeartRateSamples(UUID userId, String externalWorkoutId) {
        Workout workout = findWorkout(userId, externalWorkoutId);
        return heartRateSampleRepository.findByWorkoutIdOrderByTimestampDesc(workout.getId());
    }

    private AppUser findUser(UUID userId) {
        return appUserRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }

    private Workout findWorkout(UUID userId, String externalWorkoutId) {
        Workout workout = workoutRepository.findByUserIdAndExternalId(userId, externalWorkoutId);
        if (workout == null) {
            throw new NoSuchElementException("Workout not found: " + externalWorkoutId);
        }
        return workout;
    }
}
