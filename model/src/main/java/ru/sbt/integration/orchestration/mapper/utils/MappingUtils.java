package ru.sbt.integration.orchestration.mapper.utils;

import ru.sbt.integration.orchestration.mapper.exception.UnsupportedMappingFormatException;
import ru.sbt.integration.orchestration.mapper.model.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MappingUtils {
    //    todo - bad solution, need to fix
    public static boolean isPrimitive(Node<Field> node) {
        Class<?> type = node.getType();
        return type.isPrimitive()
                || type.equals(String.class)
                || type.equals(Integer.class)
                || type.equals(Long.class)
                || type.equals(int[].class)
                || type.equals(char[].class)
                || type.equals(Byte.class)
                || type.equals(Boolean.class)
                || type.equals(String[].class)
                || type.isEnum();
    }

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static void checkMappingErrors(Node<Field> sourceNode, Node<Field> destinationNode) {
        if (!isAssignable(sourceNode, destinationNode) && !isTypeNamesEqual(sourceNode, destinationNode)) {
            throw new UnsupportedMappingFormatException("Mapping formats cannot be assignable");
        }
    }

    private static boolean isAssignable(Node<Field> sourceNode, Node<Field> destinationNode) {
        return sourceNode.getType().isAssignableFrom(destinationNode.getType());
    }

    private static boolean isTypeNamesEqual(Node<Field> sourceNode, Node<Field> destinationNode) {
        return sourceNode.getType().getTypeName().equals(destinationNode.getType().getTypeName());
    }
}
