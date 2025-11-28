package io.github.dkuechler.longjavaty.healthmetrics.controller;

import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutHeartRateSampleResponse;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutHeartRateSamplesRequest;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutRequest;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.WorkoutResponse;
import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import io.github.dkuechler.longjavaty.healthmetrics.model.WorkoutHeartRateSample;
import io.github.dkuechler.longjavaty.healthmetrics.service.WorkoutService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> recordWorkout(@Valid @RequestBody WorkoutRequest request) {
        try {
            Workout saved = workoutService.recordWorkout(
                request.userId(),
                request.workoutType(),
                request.externalId(),
                request.startTime(),
                request.endTime(),
                request.durationSeconds(),
                request.activeDurationSeconds(),
                request.caloriesBurned(),
                request.distanceMeters(),
                request.avgHeartRate(),
                request.maxHeartRate(),
                request.minHeartRate(),
                request.routeAvailable(),
                request.sourceId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(WorkoutResponse.from(saved));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Workout already recorded for this workoutId");
        }
    }

    @GetMapping
    public List<WorkoutResponse> getWorkouts(
        @RequestParam UUID userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        try {
            return workoutService.listWorkouts(userId, from, to)
                .stream()
                .map(WorkoutResponse::from)
                .toList();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/metrics/heart-rate")
    public ResponseEntity<List<WorkoutHeartRateSampleResponse>> recordHeartRateSamples(
        @Valid @RequestBody WorkoutHeartRateSamplesRequest request
    ) {
        try {
            List<WorkoutHeartRateSample> samples = workoutService.recordHeartRateSamples(request.userId(), request.workoutId(), request.samples());
            List<WorkoutHeartRateSampleResponse> response = samples.stream().map(WorkoutHeartRateSampleResponse::from).toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Heart rate samples already recorded for this timestamp/source");
        }
    }

    @GetMapping("/metrics/heart-rate")
    public List<WorkoutHeartRateSampleResponse> getHeartRateSamples(
        @RequestParam UUID userId,
        @RequestParam String workoutId
    ) {
        try {
            return workoutService.listHeartRateSamples(userId, workoutId)
                .stream()
                .map(WorkoutHeartRateSampleResponse::from)
                .toList();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
