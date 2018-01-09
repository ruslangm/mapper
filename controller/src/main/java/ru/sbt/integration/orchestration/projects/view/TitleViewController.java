package ru.sbt.integration.orchestration.projects.view;

import ru.sbt.integration.orchestration.projects.dependency.MavenArtifact;

import java.util.List;

public class TitleViewController {
    private MavenArtifact sourceArtifact;
    private MavenArtifact destinationArtifact;
    private List<String> repos;

    public MavenArtifact getSourceArtifact() {
        return sourceArtifact;
    }

    public MavenArtifact getDestinationArtifact() {
        return destinationArtifact;
    }

    public List<String> getRepos() {
        return repos;
    }

    public void setSourceArtifact(MavenArtifact sourceArtifact) {
        this.sourceArtifact = sourceArtifact;
    }

    public void setDestinationArtifact(MavenArtifact destinationArtifact) {
        this.destinationArtifact = destinationArtifact;
    }

    public void setRepos(List<String> repos) {
        this.repos = repos;
    }
}
