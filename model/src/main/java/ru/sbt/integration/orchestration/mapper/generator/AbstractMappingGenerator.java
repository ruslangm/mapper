package ru.sbt.integration.orchestration.mapper.generator;

import ru.sbt.integration.orchestration.mapper.mapping.MultipleMappingModel;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.model.TreeModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class AbstractMappingGenerator implements MappingGenerator {
    private final List<TreeModel> sourceModelList = new ArrayList<>();
    private final TreeModel destinationModel;
    private final MultipleMappingModel mappingModel;
    private final TreeBranchList branchList = new TreeBranchList();

    AbstractMappingGenerator(MultipleMappingModel mappingModel) {
        this.mappingModel = mappingModel;
        this.sourceModelList.addAll(mappingModel.getSourceList());
        this.destinationModel = mappingModel.getDestination();
    }

    List<TreeModel> getSourceList() {
        return sourceModelList;
    }

    TreeModel getDestination() {
        return destinationModel;
    }

    public MultipleMappingModel getModel() {
        return mappingModel;
    }

    List<Node<Field>> getBranch(Node<Field> node) {
        return branchList.getBranch(node);
    }

    Node<Field> getRoot(Node<Field> node) {
        return branchList.getRoot(node);
    }

    /**
     * Inner class for presentation of route from object you need to map to it's root
     * So if you have class Organization that contains field Info that contains field Name
     * nodeBranch will contain: {Name, Field, Organization}
     */
    private class TreeBranchList {
        private final List<Node<Field>> nodeBranch = new ArrayList<>();

        private List<Node<Field>> getBranch(Node<Field> node) {
            nodeBranch.clear();
            while (node != null) {
                nodeBranch.add(node);
                node = node.getParent();
            }
            Collections.reverse(nodeBranch);
            return nodeBranch;
        }

        private Node<Field> getRoot(Node<Field> node) {
            getBranch(node);
            return nodeBranch.get(0);
        }
    }

    @Override
    public abstract void generate() throws Exception;

    @Override
    public abstract String getGeneratedCode();
}
