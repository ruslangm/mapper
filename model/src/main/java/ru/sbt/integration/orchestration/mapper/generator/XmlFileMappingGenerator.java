package ru.sbt.integration.orchestration.mapper.generator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.sbt.integration.orchestration.mapper.mapping.MultipleMappingModel;
import ru.sbt.integration.orchestration.mapper.model.Node;
import ru.sbt.integration.orchestration.mapper.model.TreeModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;

public class XmlFileMappingGenerator extends AbstractMappingGenerator {
    private Document doc;
    private final StringWriter writer = new StringWriter();

    public XmlFileMappingGenerator(MultipleMappingModel model) {
        super(model);
    }

    @Override
    public void generate() throws Exception {
        writer.getBuffer().setLength(0);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();

        generateRootTag();
        for (TreeModel treeModel : getSourceList()) {
//            TODO: FIX
            if (isMappingContains(treeModel))
                generateMappingTags(treeModel);
        }
        configureXML();
    }

    @Override
    public String getGeneratedCode() {
        return writer.toString();
    }

    private void generateRootTag() {
        Element mappingsTextElement = doc.createElement("mappings");
        Attr xmlns = doc.createAttribute("xmlns");
        xmlns.setValue("http://dozer.sourceforge.net");
        Attr xsi = doc.createAttribute("xmlns:xsi");
        xsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
        Attr schemaLocation = doc.createAttribute("xsi:schemaLocation");
        schemaLocation.setValue("http://dozer.sourceforge.net " +
                "http://dozer.sourceforge.net/schema/beanmapping.xsd");

        mappingsTextElement.setAttributeNode(xmlns);
        mappingsTextElement.setAttributeNode(xsi);
        mappingsTextElement.setAttributeNode(schemaLocation);
        doc.appendChild(mappingsTextElement);
    }

    private boolean isMappingContains(TreeModel sourceElement) {
        for (Node<Field> key : getModel().getMappingMap().keySet()) {
            if (classesEqual(key, sourceElement)) return true;
        }
        return false;
    }

    private void generateMappingTags(TreeModel sourceElement) {
        Element mappingElement = doc.createElement("mapping");
        doc.getDocumentElement().appendChild(mappingElement);

        Element sourceClassElement = doc.createElement("class-a");
        sourceClassElement.appendChild(doc.createTextNode(sourceElement.getCanonicalObjectName()));
        mappingElement.appendChild(sourceClassElement);

        Element destinationClassElement = doc.createElement("class-b");
        destinationClassElement.appendChild(doc.createTextNode(getDestination().getCanonicalObjectName()));
        mappingElement.appendChild(destinationClassElement);

        generateFieldsTag(mappingElement, sourceElement);
    }

    private void generateFieldsTag(Element root, TreeModel source) {
        getModel().getMappingMap().forEach((sourceNode, destinationNode) -> {
            if (classesEqual(sourceNode, source)) {
                generateFieldsMappingTag(sourceNode, destinationNode, root);
            }
        });
    }

    private void configureXML() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
    }

    private void generateFieldsMappingTag(Node<Field> sourceNode, Node<Field> destinationNode, Element root) {
        Element fieldElement = doc.createElement("field");
        String sourceName = createBranchName(sourceNode);
        String destinationName = createBranchName(destinationNode);

        Element sourceElement = doc.createElement("a");
        sourceElement.appendChild(doc.createTextNode(sourceName));
        Element destinationElement = doc.createElement("b");
        destinationElement.appendChild(doc.createTextNode(destinationName));

        fieldElement.appendChild(sourceElement);
        fieldElement.appendChild(destinationElement);
        root.appendChild(fieldElement);
    }

    private String createBranchName(Node<Field> nodes) {
        StringBuilder branchName = new StringBuilder();
        List<Node<Field>> branch = getBranch(nodes);
        for (Node<Field> node : branch) {
            branchName.append(node.getName());
            if (branch.indexOf(node) != branch.size() - 1) branchName.append(".");
        }
        return branchName.toString();
    }

    private boolean classesEqual(Node<Field> node, TreeModel sourceModel) {
        if (getRoot(node).value().getDeclaringClass().equals(sourceModel.getObject())) {
            return true;
        } else if (getRoot(node).value().getDeclaringClass().equals(sourceModel.getObject().getGenericSuperclass())) {
            return true;
        }
        return false;
    }
}
