package io.github.dkuechler.longjavaty.insights.repository;

import io.github.dkuechler.longjavaty.insights.model.AiInsightRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiInsightRequestRepository extends JpaRepository<AiInsightRequest, Long> {

    @Query("""
        SELECT r FROM AiInsightRequest r
        WHERE r.user.id = :userId AND r.success = true AND r.requestedAt > :since
        ORDER BY r.requestedAt DESC
        LIMIT 1
        """)
    Optional<AiInsightRequest> findLastSuccessfulRequest(
        @Param("userId") UUID userId,
        @Param("since") OffsetDateTime since
    );

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM AiInsightRequest r WHERE r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
