package ru.sbt.integration.orchestration.mapper.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for Node representation of Field
 * Every node has its value, parent and children
 */
public class FieldNode implements Node<Field> {
    private Field value;
    private Class<?> proxyValue;
    private Node<Field> parent;
    private final List<Node<Field>> children = new ArrayList<>();
    private Method getter;
    private Method setter;

    public FieldNode(Field value) {
        this.value = value;
    }

    public FieldNode(Class<?> proxyValue) {
        this.proxyValue = proxyValue;
    }

    public Field value() {
        return value;
    }

    public Node<Field> getParent() {
        return parent;
    }

    public List<Node<Field>> children() {
        return children;
    }

    public void setParent(Node<Field> parent) {
        this.parent = parent;
    }

    public void addChild(Field child) {
        Node<Field> childNode = new FieldNode(child);
        childNode.setParent(this);
        children.add(childNode);
    }

    @Override
    public String getName() {
        if (value != null) return value.getName();
        else return "";
    }

    @Override
    public Method getter() {
        return getter;
    }

    @Override
    public Method setter() {
        return setter;
    }

    public void assignGetter(Method method) {
        this.getter = method;
    }

    public void assignSetter(Method method) {
        this.setter = method;
    }

    @Override
    public Class<?> getType() {
        if (value != null) return value.getType();
        else return proxyValue;
    }

    @Override
    public void setChildren(List<Node<Field>> children) {
        this.children.clear();
        this.children.addAll(children);
    }
}
