package ru.sbt.integration.orchestration.projects.view;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import ru.sbt.integration.orchestration.mapper.mapping.MultipleMappingModel;
import ru.sbt.integration.orchestration.mapper.model.FieldNode;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.utils.MappingUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MappingViewController {
    private MultipleMappingModel model;
    private Node<Field> sourceNode;
    private Node<Field> destinationNode;
    private boolean mappingWasFailed = false;

    public boolean isMappingFailed() {
        return mappingWasFailed;
    }

    public MultipleMappingModel getModel() {
        return model;
    }

    public void generateModel(List<Class<?>> sources, Class<?> destination) {
        model = new MultipleMappingModel(sources, destination);
    }

    public void addMapping() {
        model.addMapping(sourceNode, destinationNode);
    }

    public void setSourceItemClickListener(Grid.ItemClick<Node<Field>> event) {
        sourceNode = event.getItem();
    }

    public void setDestinationItemClickListener(Grid.ItemClick<Node<Field>> event) {
        destinationNode = event.getItem();
    }

    public void configureGenerateMappingButton(TextArea area) {
        try {
            model.getMappingMap().forEach(MappingUtils::checkMappingErrors);
        } catch (Exception e) {
            Notification notif = new Notification(e.getMessage(), Notification.Type.ERROR_MESSAGE);
            notif.setPosition(Position.BOTTOM_CENTER);
            notif.show(Page.getCurrent());
            area.setStyleName("exception");
            mappingWasFailed = true;
            model.getMappingMap().clear();
        }
    }

    public void configureClearMappingButton(TextArea area) {
        area.clear();
        model.getMappingMap().clear();
    }

    public void configureAddMappingButton(TextArea area) {
        if (mappingWasFailed) {
            area.clear();
            mappingWasFailed = false;
        }
        area.setStyleName("default");
        addMapping();
        if (!area.getValue().isEmpty())
            area.setValue(area.getValue() + "\n" + returnMappingMessage());
        else
            area.setValue(returnMappingMessage());
    }

    private String returnMappingMessage() {
        return String.format("%s %s -> %s %s",
                sourceNode.getType().getSimpleName(),
                sourceNode.getName(),
                destinationNode.getType().getSimpleName(),
                destinationNode.getName());
    }

    public void configureDestinationGrid(Grid<Node<Field>> destinationGrid, AbstractLayout layout) {
        configureGrid(layout, destinationGrid, model.getDestination().getNodes());
    }

    void configureSourceGrid(Grid<Node<Field>> sourceGrid, AbstractLayout layout) {
        List<Node<Field>> sourceNodes = new ArrayList<>();
//        TODO: fix me
        model.getSourceList().forEach(model -> {
            Node<Field> sourceNode = new FieldNode(model.getObject());
            sourceNode.setChildren(model.getNodes());
            sourceNodes.add(sourceNode);
        });
        configureGrid(layout, sourceGrid, sourceNodes);
    }

    private void configureGrid(AbstractLayout layout, Grid<Node<Field>> grid, List<Node<Field>> nodes) {
        grid.setHeight(100, Sizeable.Unit.PERCENTAGE);
        grid.setWidth(100, Sizeable.Unit.PERCENTAGE);
        layout.addComponent(grid);
        grid.addColumn(node -> node.getType().getSimpleName()).setCaption("Type").setMinimumWidth(100).setMaximumWidth(200);
        grid.addColumn(Node::getName).setCaption("Field name").setId("field");
        grid.setItems(nodes);
        TreeDataProvider<Node<Field>> provider = (TreeDataProvider<Node<Field>>) grid.getDataProvider();
        fillNodes(provider, nodes);
        provider.refreshAll();
    }

    private void fillNodes(TreeDataProvider<Node<Field>> dataProvider, List<Node<Field>> nodes) {
        for (Node<Field> node : nodes) {
            if (!MappingUtils.isPrimitive(node)) {
                fillChildNodes(dataProvider, node);
                nodes = node.children();
                fillNodes(dataProvider, nodes);
            }
        }
    }

    private void fillChildNodes(TreeDataProvider<Node<Field>> dataProvider, Node<Field> node) {
        TreeData<Node<Field>> data = dataProvider.getTreeData();
        data.addItems(node, node.children());
    }
}
