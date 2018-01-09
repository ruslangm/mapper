package ru.sbt.integration.orchestration.mapper.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileHelper {

    /**
     * Получить все файлы из папки и вложенных папок, удовлетворяющие переданному предикату
     * @throws IOException
     */
    public static List<String> getFilePaths(String folderPath, Predicate<? super Path> filter) throws IOException {
        return Files.walk(Paths.get(folderPath))
                .filter(Objects::nonNull)
                .filter(Files::isRegularFile)
                .filter(filter)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    /**
     * Получить все файлы из папки и вложенных папок
     * @throws IOException
     */
    public static List<String> getFilePaths(String folderPath) throws IOException {
        return Files.walk(Paths.get(folderPath))
                .filter(Objects::nonNull)
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
}
