package io.github.dkuechler.longjavaty.healthmetrics.model;

import io.github.dkuechler.longjavaty.users.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "workout",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_workout_user_external",
           columnNames = {"user_id", "external_id"}
       ),
       indexes = {
           @Index(name = "ix_workout_user_start", columnList = "user_id, start_time DESC")
       })
@Getter
@Setter
@NoArgsConstructor
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "workout_type", nullable = false, length = 50)
    private String workoutType;

    @Column(name = "external_id", nullable = false, length = 255)
    private String externalId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "active_duration_seconds")
    private Integer activeDurationSeconds;

    @Column(name = "calories_burned")
    private Double caloriesBurned;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "avg_heart_rate")
    private Integer avgHeartRate;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "min_heart_rate")
    private Integer minHeartRate;

    @Column(name = "route_available")
    private Boolean routeAvailable;

    @Column(name = "source_id", nullable = false, length = 255)
    private String sourceId;

    public Workout(AppUser user, String workoutType, String externalId, OffsetDateTime startTime, OffsetDateTime endTime,
                   Integer durationSeconds, Integer activeDurationSeconds, Double caloriesBurned, Double distanceMeters,
                   Integer avgHeartRate, Integer maxHeartRate, Integer minHeartRate, Boolean routeAvailable, String sourceId) {
        this.user = user;
        this.workoutType = workoutType;
        this.externalId = externalId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
        this.activeDurationSeconds = activeDurationSeconds;
        this.caloriesBurned = caloriesBurned;
        this.distanceMeters = distanceMeters;
        this.avgHeartRate = avgHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.minHeartRate = minHeartRate;
        this.routeAvailable = routeAvailable;
        this.sourceId = sourceId;
    }
}
