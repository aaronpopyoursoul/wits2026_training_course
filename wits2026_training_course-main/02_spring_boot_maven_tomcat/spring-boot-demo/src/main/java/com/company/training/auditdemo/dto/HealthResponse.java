package com.company.training.auditdemo.dto;

public class HealthResponse {

    private final String status;
    private final String serviceName;
    private final String timestamp;

    public HealthResponse(String status, String serviceName, String timestamp) {
        this.status = status;
        this.serviceName = serviceName;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTimestamp() {
        return timestamp;
    }
}