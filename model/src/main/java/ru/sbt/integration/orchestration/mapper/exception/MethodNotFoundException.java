package ru.sbt.integration.orchestration.mapper.exception;

public class MethodNotFoundException extends RuntimeException{
    public MethodNotFoundException() {
        super();
    }

    public MethodNotFoundException(String s) {
        super(s);
    }

    public MethodNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
