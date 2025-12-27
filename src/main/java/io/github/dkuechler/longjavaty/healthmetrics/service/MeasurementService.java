package io.github.dkuechler.longjavaty.healthmetrics.service;

import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.healthmetrics.repository.MeasurementRepository;
import io.github.dkuechler.longjavaty.users.model.AppUser;
import io.github.dkuechler.longjavaty.users.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final AppUserRepository appUserRepository;

    public MeasurementService(MeasurementRepository measurementRepository, AppUserRepository appUserRepository) {
        this.measurementRepository = measurementRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Measurement recordMeasurement(UUID userId, MeasurementType type, double value, OffsetDateTime recordedAt, String sourceId) {
        AppUser user = findUser(userId);
        OffsetDateTime timestamp = recordedAt != null ? recordedAt : OffsetDateTime.now();
        Measurement measurement = new Measurement(user, type, value, timestamp, sourceId);
        return measurementRepository.save(measurement);
    }

    @Transactional(readOnly = true)
    public List<Measurement> findMeasurements(UUID userId, MeasurementType type, OffsetDateTime from, OffsetDateTime to) {
        findUser(userId); // ensure the user exists
        OffsetDateTime rangeStart = from != null ? from : OffsetDateTime.now().minusYears(100);
        OffsetDateTime rangeEnd = to != null ? to : OffsetDateTime.now();

        if (from != null || to != null) {
            return measurementRepository.findByUserAndMeasurementTypeAndTimestampBetween(
                userId, type, rangeStart, rangeEnd
            );
        }

        AppUser user = appUserRepository.getReferenceById(userId);
        return measurementRepository.findByUserAndMeasurementTypeOrderByTimestampDesc(user, type);
    }

    private AppUser findUser(UUID userId) {
        return appUserRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("User lookup failed for ID: {}", userId);
                return new NoSuchElementException("User not found: " + userId);
            });
    }
}
