package ru.sbt.integration.orchestration.mapper.mapping;

import ru.sbt.integration.orchestration.mapper.model.FieldTreeModel;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.model.TreeModel;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class takes source and destination Class objects in constructor and represents them in their Tree presentation
 * It's general purpose - to hold mappingMap that contains all mapping goals chosen by user
 */
public class MappingModel {
    private final Map<Node<Field>, Node<Field>> mappingMap = new ConcurrentHashMap<>();
    private final TreeModel sourceModel;
    private final TreeModel destinationModel;

    public MappingModel(Class<?> source, Class<?> destination) {
        sourceModel = new FieldTreeModel(source);
        destinationModel = new FieldTreeModel(destination);
    }

    public TreeModel getSource() {
        return sourceModel;
    }

    public TreeModel getDestination() {
        return destinationModel;
    }

    public Map<Node<Field>, Node<Field>> getMappingMap() {
        return mappingMap;
    }

    public void addMapping(Field source, Field destination) {
        mappingMap.put(sourceModel.getNode(source), destinationModel.getNode(destination));
    }

    public void addMapping(Node<Field> source, Node<Field> destination) {
        mappingMap.put(source, destination);
    }
}
