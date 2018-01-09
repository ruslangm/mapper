package ru.sbt.integration.orchestration.mapper.mapping;

import ru.sbt.integration.orchestration.mapper.model.FieldTreeModel;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.model.TreeModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleMappingModel {
    private final Map<Node<Field>, Node<Field>> mappingMap = new HashMap<>();
    private final List<TreeModel> sourceModelList = new ArrayList<>();
    private final TreeModel destinationModel;

    public MultipleMappingModel(List<Class<?>> sourceClassList, Class<?> destinationClass) {
        for (Class<?> sourceClass : sourceClassList) {
            sourceModelList.add(new FieldTreeModel(sourceClass));
        }
        destinationModel = new FieldTreeModel(destinationClass);
    }

    public MultipleMappingModel(Class<?> sourceClass, Class<?> destinationClass) {
        sourceModelList.add(new FieldTreeModel(sourceClass));
        destinationModel = new FieldTreeModel(destinationClass);
    }

    public List<TreeModel> getSourceList() {
        return sourceModelList;
    }

    public TreeModel getDestination() {
        return destinationModel;
    }

    public Map<Node<Field>, Node<Field>> getMappingMap() {
        return mappingMap;
    }

    public void addMapping(Node<Field> source, Node<Field> destination) {
        mappingMap.put(source, destination);
    }

    public void addMapping(Class<?> sourceClass, Field source, Field destination) {
        TreeModel sourceModel = sourceModelList
                .stream()
                .filter(tm -> (tm.getObject().equals(sourceClass)))
                .findFirst().orElse(null);
        if (sourceModel != null) {
            mappingMap.put(sourceModel.getNode(source), destinationModel.getNode(destination));
        }
    }
}
