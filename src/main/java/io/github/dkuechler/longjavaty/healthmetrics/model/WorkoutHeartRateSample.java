package io.github.dkuechler.longjavaty.healthmetrics.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "workout_heart_rate_sample",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_workout_sample_ts_source",
           columnNames = {"workout_id", "sample_time", "source_id"}
       ),
       indexes = {
           @Index(name = "ix_workout_hr_sample_time", columnList = "workout_id, sample_time DESC")
       })
@Getter
@Setter
@NoArgsConstructor
public class WorkoutHeartRateSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @Column(name = "sample_time", nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "bpm", nullable = false)
    private Integer bpm;

    @Column(name = "source_id", nullable = false, length = 255)
    private String sourceId;

    public WorkoutHeartRateSample(Workout workout, OffsetDateTime timestamp, Integer bpm, String sourceId) {
        this.workout = workout;
        this.timestamp = timestamp;
        this.bpm = bpm;
        this.sourceId = sourceId;
    }
}
