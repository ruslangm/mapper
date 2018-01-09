package ru.sbt.integration.orchestration.mapper.mapping;

import ru.sbt.integration.orchestration.mapper.model.Node;

import java.lang.reflect.Field;

/**
 * Class for mapping purposes
 * It shouldn't be public, may be better make it with package visibility
 */
public class MappingObject {
    private final Node<Field> instance;

    public MappingObject(Node<Field> instance) {
        this.instance = instance;
    }

    public String name() {
        return type().getSimpleName().toLowerCase();
    }

    private Class<?> type() {
        return instance.value().getDeclaringClass();
    }

    public String getter() {
        return instance.getter().getName();
    }

    public String setter() {
        return instance.setter().getName();
    }
}
