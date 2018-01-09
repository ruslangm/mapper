package ru.sbt.integration.orchestration.mapper.model;

import java.lang.reflect.Method;
import java.util.List;

public interface Node<T> {
    T value();

    Node<T> getParent();

    List<Node<T>> children();

    void setParent(Node<T> parent);

    void addChild(T child);

    String getName();

    Method getter();

    Method setter();

    Class<?> getType();

    void setChildren(List<Node<T>> children);
}
