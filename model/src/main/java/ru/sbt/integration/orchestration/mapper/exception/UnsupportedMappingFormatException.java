package ru.sbt.integration.orchestration.mapper.exception;

public class UnsupportedMappingFormatException extends RuntimeException {
    public UnsupportedMappingFormatException() {
        super();
    }

    public UnsupportedMappingFormatException(String s) {
        super(s);
    }

    public UnsupportedMappingFormatException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
