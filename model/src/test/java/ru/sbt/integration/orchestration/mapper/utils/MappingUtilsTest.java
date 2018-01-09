package ru.sbt.integration.orchestration.mapper.utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import ru.sbt.integration.orchestration.mapper.model.FieldNode;
import ru.sbt.integration.orchestration.mapper.model.FieldTreeModel;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.model.TreeModel;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;


public class MappingUtilsTest {
    private static TreeModel model;


    @Test
    public void isAssignable() throws Exception {
        Field field0 = FieldUtils.getAllFields(Integer.class)[0];
        Field field1 = FieldUtils.getAllFields(Integer.class)[3];
        Field field2 = FieldUtils.getAllFields(String.class)[0];
        Node<Field> node0 = new FieldNode(field0);
        Node<Field> node1 = new FieldNode(field1);
        Node<Field> node2 = new FieldNode(field2);
        Assert.assertNotNull(node0);
        Assert.assertNotNull(node1);
        Assert.assertNotNull(node2);
        Assert.assertEquals(true, MappingUtils.isPrimitive(node0));
        Assert.assertEquals(true, MappingUtils.isPrimitive(node1));
        Assert.assertEquals(false, MappingUtils.isPrimitive(node2));
    }

}