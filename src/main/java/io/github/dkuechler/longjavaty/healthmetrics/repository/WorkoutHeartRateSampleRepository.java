package io.github.dkuechler.longjavaty.healthmetrics.repository;

import io.github.dkuechler.longjavaty.healthmetrics.model.WorkoutHeartRateSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutHeartRateSampleRepository extends JpaRepository<WorkoutHeartRateSample, Long> {

    @Query("SELECT s FROM WorkoutHeartRateSample s WHERE s.workout.id = :workoutId ORDER BY s.timestamp DESC")
    List<WorkoutHeartRateSample> findByWorkoutIdOrderByTimestampDesc(@Param("workoutId") Long workoutId);
}
