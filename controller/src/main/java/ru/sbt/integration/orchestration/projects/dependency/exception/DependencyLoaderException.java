package ru.sbt.integration.orchestration.projects.dependency.exception;

public class DependencyLoaderException extends RuntimeException {
    public DependencyLoaderException() {
        super();
    }

    public DependencyLoaderException(String s) {
        super(s);
    }

    public DependencyLoaderException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
