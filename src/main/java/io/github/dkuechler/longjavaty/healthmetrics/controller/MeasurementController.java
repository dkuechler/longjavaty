package io.github.dkuechler.longjavaty.healthmetrics.controller;

import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.MeasurementRequest;
import io.github.dkuechler.longjavaty.healthmetrics.controller.dto.MeasurementResponse;
import io.github.dkuechler.longjavaty.healthmetrics.model.Measurement;
import io.github.dkuechler.longjavaty.healthmetrics.model.MeasurementType;
import io.github.dkuechler.longjavaty.healthmetrics.service.MeasurementService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/measurements")
@lombok.extern.slf4j.Slf4j
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping
    public ResponseEntity<MeasurementResponse> recordMeasurement(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt,
            @Valid @RequestBody MeasurementRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        log.info("Received measurement request for user: {}, type: {}", userId, request.measurementType());
        
        try {
            Measurement saved = measurementService.recordMeasurement(
                    userId,
                    request.measurementType(),
                    request.value(),
                    request.recordedAt(),
                    request.sourceId());
            log.debug("Successfully recorded measurement: {}", saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(MeasurementResponse.from(saved));
        } catch (NoSuchElementException e) {
            log.warn("Failed to record measurement - user not found: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate measurement attempt for user: {} and source: {}", userId, request.sourceId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Measurement already recorded for this source");
        }
    }

    @GetMapping
    public List<MeasurementResponse> getMeasurements(
            @AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt,
            @RequestParam MeasurementType measurementType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        UUID userId = UUID.fromString(jwt.getSubject());
        log.info("Fetching measurements for user: {}, type: {}, range: {} to {}", userId, measurementType, from, to);
        try {
            return measurementService.findMeasurements(userId, measurementType, from, to)
                    .stream()
                    .map(MeasurementResponse::from)
                    .toList();
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
