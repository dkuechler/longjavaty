package io.github.dkuechler.longjavaty.insights.model;

import io.github.dkuechler.longjavaty.users.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_insight_request",
       indexes = {
           @Index(name = "ix_ai_insight_user_time", columnList = "user_id, requested_at DESC")
       })
@Getter
@Setter
@NoArgsConstructor
public class AiInsightRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "requested_at", nullable = false)
    private OffsetDateTime requestedAt;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "model_used", length = 50)
    private String modelUsed;

    @Column(name = "success", nullable = false)
    private boolean success;

    public AiInsightRequest(AppUser user, OffsetDateTime requestedAt) {
        this.user = user;
        this.requestedAt = requestedAt;
        this.success = false;
    }

    public void markSuccessful(Integer tokensUsed, String modelUsed) {
        this.success = true;
        this.tokensUsed = tokensUsed;
        this.modelUsed = modelUsed;
    }
}
