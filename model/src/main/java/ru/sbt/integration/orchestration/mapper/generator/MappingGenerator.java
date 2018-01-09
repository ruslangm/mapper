package ru.sbt.integration.orchestration.mapper.generator;

public interface MappingGenerator {
    void generate() throws Exception;

    String getGeneratedCode();
}
