package io.github.dkuechler.longjavaty.healthmetrics.model;

public enum MeasurementType {
    HEART_RATE("bpm"),
    RESTING_HEART_RATE("bpm"),
    VO2_MAX("ml/kg/min"),
    STEPS("count");
    
    private final String unit;
    
    MeasurementType(String unit) {
        this.unit = unit;
    }
    
    public String getUnit() {
        return unit;
    }
}
