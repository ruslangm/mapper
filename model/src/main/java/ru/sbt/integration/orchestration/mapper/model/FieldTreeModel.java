package ru.sbt.integration.orchestration.mapper.model;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.Nullable;
import org.reflections.ReflectionUtils;
import ru.sbt.integration.orchestration.mapper.exception.FieldNotFoundException;
import ru.sbt.integration.orchestration.mapper.utils.MappingUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Class for Tree representation of Class object
 */
public class FieldTreeModel implements TreeModel {
    private final Class<?> object;
    private final List<Node<Field>> nodes = new ArrayList<>();
    private Node<Field> foundNode = null;

    public FieldTreeModel(Class<?> object) {
        this.object = object;
        initializeModel();
//        Node<Field> node = new FieldNode(object.);
    }

    @Override
    public List<Node<Field>> getNodes() {
        return nodes;
    }

    @Override
    public Class<?> getObject() {
        return object;
    }

    @Override
    public String getSimpleObjectName() {
        return object.getSimpleName();
    }

    @Override
    public String getCanonicalObjectName() {
        return object.getCanonicalName();
    }

    /**
     * Generates children of chosen class object
     */
    private void initializeModel() {
        for (Field field : getFields()) {
            Node<Field> node = new FieldNode(field);
            if (!MappingUtils.isStatic(node.value())) {
                node.setParent(null);
                assignMethods(node);
                nodes.add(node);
            }
        }
        generateNodes();
    }

    private void generateNodes() {
        generateNodes(nodes);
    }

    /**
     * Recursively generates children of child nodes
     */
    private void generateNodes(List<Node<Field>> nodes) {
        for (Node<Field> node : nodes) {
            assignMethods(node);
            if (isRecursion(node))
                continue;
            //warning! если убрать условие - работает намного медленнее
            if (!MappingUtils.isPrimitive(node) && !MappingUtils.isStatic(node.value())) {
                addChildNodes(node, getFields(node));
                nodes = node.children();
                generateNodes(nodes);
            }
        }
    }

    private void addChildNodes(Node<Field> node, List<Field> childNodes) {
        for (Field f : childNodes) {
            if (!MappingUtils.isStatic(f)) node.addChild(f);
        }
    }

    /**
     * Method checks is node containing as field in one of its parent
     */
    private boolean isRecursion(Node<Field> node) {
        Node<Field> parent = null;
        Node<Field> parentOfParent = null;
        if (node.getParent() != null) {
            parent = node.getParent();
            parentOfParent = node.getParent().getParent();
        }
        return parentOfParent != null
                && (node.getType().equals(parent.getType())
                || node.getType().equals(parentOfParent.getType()));
    }

    private List<Field> getFields() {
        return FieldUtils.getAllFieldsList(object);
//        return Arrays.asList(object.getDeclaredFields());
    }

    private List<Field> getFields(Node<Field> node) {
        return FieldUtils.getAllFieldsList((node.value()).getType());
//        return Arrays.asList(node.getType().getDeclaredFields());
    }

    /**
     * Method for representing given field as a node
     */
    @Override
    public Node<Field> getNode(Field field) {
        foundNode = null;
        Node<Field> node = getNode(field, null, nodes);
        if (node != null)
            return node;
        else
            throw new FieldNotFoundException();
    }

    /**
     * Method for representing given fieldName as a node
     */
    @Override
    public Node<Field> getNode(String fieldName) {
        foundNode = null;
        Node<Field> node = getNode(null, fieldName, nodes);
        if (node != null)
            return node;
        else
            throw new FieldNotFoundException();
    }

    /**
     * Method recursively searches given field in TreeModel of it's source Class object
     * foundNode object needs to break out of a recursion
     */
    @Nullable
    private Node<Field> getNode(Field field, String fieldName, List<Node<Field>> nodes) {
        for (Node<Field> node : nodes) {
            if (isRecursion(node)) continue;
            if (isNodeEquals(node, field, fieldName)) {
                foundNode = node;
                return foundNode;
            } else {
                nodes = node.children();
                getNode(field, fieldName, nodes);
            }
            if (foundNode != null)
                return foundNode;
        }
        return null;
    }

    private boolean isNodeEquals(Node<Field> node, Field field, String fieldName) {
        if (field != null)
            return node.value().equals(field);
        else
            return node.value().getName().equals(fieldName);
    }

    private void assignMethods(Node<Field> node) {
        assignGetter(node);
        assignSetter(node);
    }

    /**
     * Method gets all methods with given strategy (is it getter or setter) for given node
     * Then it finds necessary method from set of all methods and assigns it to node
     */
    private void assignGetter(Node<Field> node) {
        Set<Method> getters = getAllMethodsWithStrategy(node, Strategy.GET);
        Method getter = findCorrespondingMethod(getters, node, Strategy.GET);
        ((FieldNode) node).assignGetter(getter);
    }

    private void assignSetter(Node<Field> node) {
        Set<Method> setters = getAllMethodsWithStrategy(node, Strategy.SET);
        Method setter = findCorrespondingMethod(setters, node, Strategy.SET);
        ((FieldNode) node).assignSetter(setter);
    }

    private Set<Method> getAllMethodsWithStrategy(Node<Field> node, Strategy strategy) {
        if (strategy.equals(Strategy.GET)) {
            return ReflectionUtils.getAllMethods(node.value().getDeclaringClass(),
                    ReflectionUtils.withModifier(Modifier.PUBLIC),
                    ReflectionUtils.withPrefix(strategy.toString().toLowerCase()),
                    ReflectionUtils.withReturnTypeAssignableTo(node.getType()));
        } else {
            return ReflectionUtils.getAllMethods(node.value().getDeclaringClass(),
                    ReflectionUtils.withModifier(Modifier.PUBLIC),
                    ReflectionUtils.withPrefix(strategy.toString().toLowerCase()),
                    ReflectionUtils.withReturnTypeAssignableTo(void.class));
        }
    }

    @Nullable
    private Method findCorrespondingMethod(Set<Method> methods, Node<Field> node, Strategy strategy) {
        for (Method method : methods) {
            if (isMethodFound(method, node, strategy))
                return method;
        }
        return null;
    }

    private boolean isMethodFound(Method method, Node<Field> node, Strategy strategy) {
        return method.getName().equalsIgnoreCase(strategy + node.value().getName());
    }
}
