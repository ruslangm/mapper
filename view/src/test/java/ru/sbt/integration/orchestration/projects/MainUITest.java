package ru.sbt.integration.orchestration.projects;

import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.VerticalLayout;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.model.TreeModel;

import java.lang.reflect.Field;

public class MainUITest {
    private TreeGrid<Node<Field>> grid;
    final VerticalLayout layout = new VerticalLayout();
    private static TreeModel modelSource;
    private static TreeModel modelDest;

    @BeforeClass
    public static void generateModel() {
//        modelSource = new FieldTreeModel(Organization.class);
//        modelDest = new FieldTreeModel(NewOrganization.class);
    }

    @Test
    public void generate() {
//        grid = new TreeGrid<>();
//        layout.addComponent(grid);
//        grid.addColumn(Node::getName).setCaption("Fields - " + modelSource.getObject().getSimpleName());
//        grid.setItems(modelSource.getNodes());
//
//        TreeDataProvider<Node<Field>> provider = (TreeDataProvider<Node<Field>>) grid.getDataProvider();
//
//        TreeData<Node<Field>> data = provider.getTreeData();
//
//        data.addItems(modelSource.getNodes().get(10), modelSource.getNodes().get(0).children());
//
//        grid.expand(modelSource.getNodes().get(0).children());
//
//        grid.expand(modelSource.getNodes().get(0));
//
//        provider.refreshAll();
    }
}