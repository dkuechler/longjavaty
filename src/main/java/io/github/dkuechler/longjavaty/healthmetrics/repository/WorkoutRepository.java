package io.github.dkuechler.longjavaty.healthmetrics.repository;

import io.github.dkuechler.longjavaty.healthmetrics.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserIdOrderByStartTimeDesc(UUID userId);

    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.startTime BETWEEN :from AND :to ORDER BY w.startTime DESC")
    List<Workout> findByUserIdAndStartTimeBetween(
        @Param("userId") UUID userId,
        @Param("from") OffsetDateTime from,
        @Param("to") OffsetDateTime to
    );

    @Query("SELECT w FROM Workout w WHERE w.user.id = :userId AND w.externalId = :externalId")
    Workout findByUserIdAndExternalId(@Param("userId") UUID userId, @Param("externalId") String externalId);
}
