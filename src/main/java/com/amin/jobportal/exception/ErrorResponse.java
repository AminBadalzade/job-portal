package com.amin.jobportal.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
    private List<String> errors;

    public ErrorResponse(String message, int status, LocalDateTime timestamp, String path) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    public ErrorResponse(String message, int status, LocalDateTime timestamp, String path, List<String> errors) {
        this(message, status, timestamp, path);
        this.errors = errors;
    }
}

