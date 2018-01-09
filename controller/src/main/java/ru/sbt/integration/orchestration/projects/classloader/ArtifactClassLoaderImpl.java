package ru.sbt.integration.orchestration.projects.classloader;

import ru.sbt.integration.orchestration.mapper.utils.FileHelper;
import ru.sbt.integration.orchestration.projects.dependency.Artifact;
import ru.sbt.integration.orchestration.projects.dependency.DependencyLoader;
import ru.sbt.integration.orchestration.projects.dependency.DependencyLoaderImpl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ArtifactClassLoaderImpl implements ArtifactClassLoader {
    private final String DEFAULT_LOCAL_REPO_PATH = "target/downloaded-sources";
    private final DependencyLoader dependencyLoader = new DependencyLoaderImpl();

    @Override
    public List<Class<?>> getClasses(Set<Artifact> artifacts, List<String> repoList) throws IOException {
        return getClasses(artifacts, repoList, DEFAULT_LOCAL_REPO_PATH);
    }

    @Override
    public List<Class<?>> getClasses(Artifact artifact, List<String> repoList) throws IOException {
        return getClasses(artifact, repoList, DEFAULT_LOCAL_REPO_PATH);
    }

    @Override
    public List<Class<?>> getClasses(Artifact artifact, List<String> repoList, String localRepoPath) throws IOException {
        Set<Artifact> artifacts = new HashSet<>();
        artifacts.add(artifact);
        return getClasses(artifacts, repoList, localRepoPath);
    }

    @Override
    public List<Class<?>> getClasses(Set<Artifact> artifacts, List<String> repoList, String localRepoPath) throws IOException {
        dependencyLoader.resolveDependencies(artifacts, repoList, localRepoPath);
        List<String> jarPaths = FileHelper.getFilePaths(localRepoPath, file -> file.toString().endsWith(".jar"));
        ClassLoader cl = getClassLoaderForJarFile(jarPaths.toArray(new String[jarPaths.size()]));
        List<Class<?>> classList = new ArrayList<>();

        for (Artifact artifact : artifacts) {
            getJarFile(jarPaths, cl, classList, artifact);
        }

        return classList;
    }

    private ClassLoader getClassLoaderForJarFile(String... paths) throws MalformedURLException {
        URL[] classLoaderUrls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            File file = new File(paths[i]);
            classLoaderUrls[i] = file.toURI().toURL();
        }
        return new URLClassLoader(classLoaderUrls);
    }

    private void getJarFile(List<String> jarPaths, ClassLoader loader, List<Class<?>> classList, Artifact artifact) throws IOException {
        jarPaths.stream()
                .filter(s -> s.contains(artifact.getArtifactId() + "-" + artifact.getVersion() + ".jar"))
                .findFirst()
                .ifPresent(jarFileName -> loadClasses(jarFileName, loader, classList));
    }

    private void loadClasses(String fileName, ClassLoader loader, List<Class<?>> classList) {
        try (JarFile jar = new JarFile(fileName)) {
            jar.stream()
                    .filter(Objects::nonNull)
                    .map(JarEntry::toString)
                    .filter(entry -> entry.endsWith(".class"))
                    .forEach(clazz -> loadClass(loader, classList, clazz));
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void loadClass(ClassLoader loader, List<Class<?>> classList, String clazz) {
        try {
            Class<?> loadedClass = loader.loadClass(clazz.replace("/", ".").replace(".class", ""));
            classList.add(loadedClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
