package ru.sbt.integration.orchestration.projects.classloader;

import ru.sbt.integration.orchestration.projects.dependency.Artifact;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ArtifactClassLoader {
    List<Class<?>> getClasses(Artifact artifact, List<String> repoList) throws IOException;

    List<Class<?>> getClasses(Artifact artifact, List<String> repoList, String localRepoPath) throws IOException;

    List<Class<?>> getClasses(Set<Artifact> artifacts, List<String> repoList) throws IOException;

    List<Class<?>> getClasses(Set<Artifact> artifacts, List<String> repoList, String localRepoPath) throws IOException;
}
