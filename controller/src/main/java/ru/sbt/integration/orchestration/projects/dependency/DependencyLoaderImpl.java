package ru.sbt.integration.orchestration.projects.dependency;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import ru.sbt.integration.orchestration.projects.dependency.exception.DependencyLoaderException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyLoaderImpl implements DependencyLoader {
    private final CollectRequest collectRequest = new CollectRequest();
    private final DependencyRequest request = new DependencyRequest();
    private final DefaultServiceLocator locator = getServiceLocator();
    private final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    private final PreorderNodeListGenerator preorderNodeListGenerator = new PreorderNodeListGenerator();

    @Override
    public String resolveDependencies(Set<Artifact> artifacts, List<String> repoList, String localRepoPath) throws DependencyLoaderException {
        try {
            collectRepositories(repoList);
            uploadArtifactWithDependencies(artifacts, localRepoPath);
            return preorderNodeListGenerator.getClassPath();
        } catch (Exception e) {
            throw new DependencyLoaderException(e.getMessage(), e);
        }
    }

    @Override
    public String resolveDependencies(Artifact artifact, List<String> repoList, String localRepoPath) throws DependencyLoaderException {
        Set<Artifact> artifacts = new HashSet<>();
        artifacts.add(artifact);
        return resolveDependencies(artifacts, repoList, localRepoPath);
    }

    private void collectRepositories(List<String> repoList) {
        collectRequest.setRepositories(repoList.stream()
                .map(url -> {
                    String id = getRepositoryId(url);
                    return new RemoteRepository.Builder(id, "default", url).build();
                })
                .distinct()
                .collect(Collectors.toList()));
    }

    private RepositorySystem getRepositorySystem(String localRepositoryPath, DefaultServiceLocator locator, DefaultRepositorySystemSession session) {
        RepositorySystem system = locator.getService(RepositorySystem.class);
        LocalRepository localRepository = new LocalRepository(localRepositoryPath);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepository));
        return system;
    }

    private DefaultServiceLocator getServiceLocator() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        return locator;
    }

    private void uploadArtifactWithDependencies(Set<Artifact> artifacts, String localRepositoryPath) throws Exception {
        for (Artifact artifact : artifacts) {
            uploadArtifactWithDependencies(artifact, localRepositoryPath);
        }
    }

    private void uploadArtifactWithDependencies(Artifact artifact, String localRepositoryPath) throws Exception{
        DefaultArtifact defaultArtifact = new DefaultArtifact(String.format("%s:%s:%s",
                artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion()));
        Dependency dependency = new Dependency(defaultArtifact, "compile");
        collectRequest.setRoot(dependency);
        RepositorySystem system = getRepositorySystem(localRepositoryPath, locator, session);
        DependencyNode dependencyNode = system.collectDependencies(session, collectRequest).getRoot();
        request.setRoot(dependencyNode);
        system.resolveDependencies(session, request);
        dependencyNode.accept(preorderNodeListGenerator);
    }

    private String getRepositoryId(String url) {
        if (url.endsWith("/")) {
            String part = url.substring(0, url.length() - 1);
            return  part.substring(part.lastIndexOf("/") + 1);
        } else {
            return  url.substring(url.lastIndexOf("/") + 1);
        }
    }
}
