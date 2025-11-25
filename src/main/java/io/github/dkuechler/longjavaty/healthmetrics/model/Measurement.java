package io.github.dkuechler.longjavaty.healthmetrics.model;

import io.github.dkuechler.longjavaty.users.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "measurement",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_measurement_dedup",
           columnNames = {"user_id", "measurement_type", "external_source_id"}
       ),
       indexes = {
           @Index(name = "ix_measurement_user_type_ts",
                  columnList = "user_id, measurement_type, recorded_at DESC")
       })
@Getter @Setter @NoArgsConstructor
public class Measurement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false)
    private MeasurementType measurementType;
    
    @Column(name = "value_numeric", nullable = false)
    private Double value;
    
    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime timestamp;
    
    @Column(name = "external_source_id", nullable = false, length = 255)
    private String sourceId;
    
    public Measurement(AppUser user, MeasurementType measurementType, Double value, 
                      OffsetDateTime timestamp, String sourceId) {
        this.user = user;
        this.measurementType = measurementType;
        this.value = value;
        this.timestamp = timestamp;
        this.sourceId = sourceId;
    }
    
    public String getUnit() {
        return measurementType.getUnit();
    }
}
