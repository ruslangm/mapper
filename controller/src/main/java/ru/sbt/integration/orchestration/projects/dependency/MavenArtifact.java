package ru.sbt.integration.orchestration.projects.dependency;

public class MavenArtifact implements Artifact{
    private final String groupId;
    private final String artifactId;
    private final String version;

    public MavenArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }
}
