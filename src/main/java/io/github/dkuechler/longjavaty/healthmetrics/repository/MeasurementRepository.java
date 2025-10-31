package io.github.dkuechler.longjavaty.healthmetrics.repository;

import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    
    List<Measurement> findByUserAndMeasurementTypeOrderByTimestampDesc(AppUser user, MeasurementType measurementType);
    
    @Query("SELECT m FROM Measurement m WHERE m.user.id = :userId AND m.measurementType = :measurementType AND m.timestamp BETWEEN :from AND :to ORDER BY m.timestamp DESC")
    List<Measurement> findByUserAndMeasurementTypeAndTimestampBetween(
        @Param("userId") UUID userId, 
        @Param("measurementType") MeasurementType measurementType, 
        @Param("from") OffsetDateTime from, 
        @Param("to") OffsetDateTime to
    );
    
    @Query("SELECT m FROM Measurement m WHERE m.user.id = :userId AND m.measurementType = :measurementType ORDER BY m.timestamp DESC LIMIT 1")
    Measurement findLatestByUserAndMeasurementType(@Param("userId") UUID userId, @Param("measurementType") MeasurementType measurementType);
}