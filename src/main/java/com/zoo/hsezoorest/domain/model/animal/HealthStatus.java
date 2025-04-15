package com.zoo.hsezoorest.domain.model.animal;

public enum HealthStatus {
    HEALTHY("Healthy"),
    SICK("Sick"),
    UNDER_OBSERVATION("Under Observation");

    private final String displayName;

    HealthStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
