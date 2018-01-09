package ru.sbt.integration.orchestration.mapper.model;

import java.lang.reflect.Field;
import java.util.List;

public interface TreeModel {
    List<Node<Field>> getNodes();

    Class<?> getObject();

    String getSimpleObjectName();

    String getCanonicalObjectName();

    Node<Field> getNode(Field field);

    Node<Field> getNode(String name);
}
