package com.company.training.auditdemo.dto;

public class SystemMessageResponse {

    private final String message;
    private final String module;
    private final String timestamp;
    private final String traceability;

    public SystemMessageResponse(String message,
                                 String module,
                                 String timestamp,
                                 String traceability) {
        this.message = message;
        this.module = module;
        this.timestamp = timestamp;
        this.traceability = traceability;
    }

    public String getMessage() {
        return message;
    }

    public String getModule() {
        return module;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTraceability() {
        return traceability;
    }
}