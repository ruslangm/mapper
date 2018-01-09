package ru.sbt.integration.orchestration.projects.dependency;

import java.util.List;
import java.util.Set;

/**
 * Created by SBT-Vitchinkin-AV on 14.09.2017.
 */
public interface DependencyLoader {
    String resolveDependencies(Set<Artifact> artifacts, List<String> repoList, String localRepoPath);
    String resolveDependencies(Artifact artifact, List<String> repoList, String localRepoPath);
}
