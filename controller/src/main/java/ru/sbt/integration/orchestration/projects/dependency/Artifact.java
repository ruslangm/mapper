package ru.sbt.integration.orchestration.projects.dependency;

public interface Artifact {
    String getGroupId();

    String getArtifactId();

    String getVersion();
}
