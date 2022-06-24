package com.tracer.agent;

public class TracerException extends RuntimeException {

    public TracerException(String message) {
        super(message);
    }

    public TracerException(Throwable cause) {
        super(cause);
    }

    public TracerException(String message, Throwable cause) {
        super(message, cause);
    }
}
