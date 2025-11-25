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
           name = "uk_workout_user_source",
           columnNames = {"user_id", "source_id"}
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

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "calories_burned")
    private Double caloriesBurned;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "source_id", nullable = false, length = 255)
    private String sourceId;

    public Workout(AppUser user, String workoutType, OffsetDateTime startTime, Integer durationSeconds,
                   Double caloriesBurned, Double distanceMeters, String sourceId) {
        this.user = user;
        this.workoutType = workoutType;
        this.startTime = startTime;
        this.durationSeconds = durationSeconds;
        this.caloriesBurned = caloriesBurned;
        this.distanceMeters = distanceMeters;
        this.sourceId = sourceId;
    }
}
