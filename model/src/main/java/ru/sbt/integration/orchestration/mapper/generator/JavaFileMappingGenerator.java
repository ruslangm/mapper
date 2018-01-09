package ru.sbt.integration.orchestration.mapper.generator;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.jetbrains.annotations.NotNull;
import ru.sbt.integration.orchestration.mapper.mapping.MappingObject;
import ru.sbt.integration.orchestration.mapper.mapping.MultipleMappingModel;
import ru.sbt.integration.orchestration.mapper.model.Node;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for generating java code from Tree class presentation
 */
public class JavaFileMappingGenerator extends AbstractMappingGenerator {
    private final StringBuilder generatedCode = new StringBuilder();

    public JavaFileMappingGenerator(MultipleMappingModel mappingModel) {
        super(mappingModel);
    }

    @Override
    public void generate() {
        generateJavaFile();
    }

    @Override
    public String getGeneratedCode() {
//        generateJavaFile();
        return generatedCode.toString();
    }

    /**
     * Method for generating a JavaFile containing a single class with single map method
     */
    private void generateJavaFile() {
        List<Class<?>> sourceParametersList = new ArrayList<>();
        List<MethodSpec> methodsList = new ArrayList<>();
        String destinationParam = getDestination().getSimpleObjectName().toLowerCase();

        getModel().getMappingMap().forEach((s, d) -> {
            Class<?> sourceClass = getRoot(s).value().getDeclaringClass();
            sourceParametersList.add(sourceClass);
        });

        sourceParametersList.forEach(clazz ->
                methodsList.add(generateMethodBody(clazz, clazz.getSimpleName().toLowerCase(), destinationParam)));

        TypeSpec mappingClass = TypeSpec.classBuilder("Mapping")
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodsList)
                .build();

        JavaFile javaFile = JavaFile
                .builder("ru.sbt.integration.orchestration.mapping", mappingClass)
                .build();

        generatedCode.setLength(0);
        generatedCode.append(javaFile.toString());
    }

    /**
     * Method for generating java code for every mapping goal
     */
    private MethodSpec generateMethodBody(Class<?> sourceClass, String sourceParameter, String destinationParameter) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("map_" + sourceParameter)
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(sourceClass, sourceParameter)
                .addParameter(getDestination().getObject(), destinationParameter);

        for (Map.Entry<Node<Field>, Node<Field>> entry : getModel().getMappingMap().entrySet()) {
            if (getRoot(entry.getKey()).value().getDeclaringClass().equals(sourceClass)) {
                methodBuilder.addCode(generateMappingBlock(entry.getKey(), entry.getValue()));
            }
        }

        return methodBuilder.build();
    }

    /**
     * generates mapping code block for given nodes
     * you need to firstly call generateDeclarations(sourceNode, destinationNode) then
     * generateSetterCallsBranch(destinationNode) and only then generateGetterCallsBranch(sourceNode)
     */
    @NotNull
    private CodeBlock generateMappingBlock(Node<Field> sourceNode, Node<Field> destinationNode) {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add(generateSetterCallsBranch(getBranch(destinationNode)));
        codeBuilder.add(generateGetterCallsBranch(getBranch(sourceNode)));
        return codeBuilder.build();
    }

    /**
     * returns branch of get method calls for specific object presented in java code.
     * It looks like:
     * Client client = new Client();
     * client.getInfo().getName().getPassword()
     */
    @NotNull
    private CodeBlock generateGetterCallsBranch(List<Node<Field>> sourceBranch) {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();

        for (Node<Field> node : sourceBranch) {
            MappingObject instance = new MappingObject(node);
            if (node.getParent() == null) {
                codeBuilder.add("$L.$L()", instance.name(), instance.getter());
            } else {
                codeBuilder.add(".$L()", instance.getter());
            }
        }
        //todo - bad practice
        codeBuilder.add(")");
        codeBuilder.addStatement("");
        return codeBuilder.build();
    }

    /**
     * returns branch of get and set method calls for specific object presented in java code.
     * For example, if you need to call setter on some specific field of class,
     * firstly you should call all getters of all its root fields and then call setter.
     * It looks like:
     * Organization org = new Organization();
     * org.getInfo().getName().setPassword(client.getInfo().getName().getPassword())
     * where argument in setter - is value you get from calls of multiple getters on source object
     */
    @NotNull
    private CodeBlock generateSetterCallsBranch(List<Node<Field>> destinationBranch) {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();

        for (Node<Field> node : destinationBranch) {
            MappingObject instance = new MappingObject(node);
            if (node.getParent() == null)
                codeBuilder.add("$L", instance.name());
            if (destinationBranch.indexOf(node) == destinationBranch.size() - 1)
                codeBuilder.add(".$L(", instance.setter());
            else codeBuilder.add(".$L()", instance.getter());
        }

        return codeBuilder.build();
    }
}
