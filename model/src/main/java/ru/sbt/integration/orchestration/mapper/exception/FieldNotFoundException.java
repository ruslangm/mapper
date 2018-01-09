package ru.sbt.integration.orchestration.mapper.exception;

public class FieldNotFoundException extends RuntimeException {
    public FieldNotFoundException() {
        super();
    }

    public FieldNotFoundException(String s) {
        super(s);
    }

    public FieldNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
