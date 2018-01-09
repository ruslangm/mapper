package ru.sbt.integration.orchestration.projects.view;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.codehaus.plexus.util.StringInputStream;
import org.jetbrains.annotations.NotNull;
import ru.sbt.integration.orchestration.mapper.generator.JavaFileMappingGenerator;
import ru.sbt.integration.orchestration.mapper.generator.MappingGenerator;
import ru.sbt.integration.orchestration.mapper.generator.XmlFileMappingGenerator;
import ru.sbt.integration.orchestration.mapper.mapping.MultipleMappingModel;
import ru.sbt.integration.orchestration.projects.view.utils.BaseViewUtils;

import java.io.InputStream;

public class GeneratedMappingView extends VerticalLayout implements View {
    private MappingGenerator generator;
    private String fileName = "Mapping.java";
    private StreamResource resource = new StreamResource(this::getStream, fileName);
    private FileDownloader downloader = new FileDownloader(resource);

    GeneratedMappingView(MultipleMappingModel model, Navigator navigator) {
        setSizeFull();
        generator = new JavaFileMappingGenerator(model);
        generateMapping();

        Label label = new Label("<h1>Generated mapping code<h1>", ContentMode.HTML);
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        TextArea area = createTextArea();
        area.setWidth(70, Unit.PERCENTAGE);
        area.setRows(20);
        RadioButtonGroup<String> radioFileChooser = createRadioButtonGroup(model, area);

        Button downloadButton = new Button("Download file");
        downloadButton.setWidth(10, Unit.PICAS);
        downloadButton.addClickListener(event -> {
            resource = new StreamResource(() ->
                        new StringInputStream(generator.getGeneratedCode()), fileName);
            downloader.setFileDownloadResource(resource);
        });

        downloader.extend(downloadButton);

        mainLayout.addComponent(area);
        mainLayout.addComponent(radioFileChooser);
        mainLayout.setExpandRatio(area, 3);
        mainLayout.setExpandRatio(radioFileChooser, 1);
        mainLayout.setComponentAlignment(area, Alignment.MIDDLE_RIGHT);
        mainLayout.setComponentAlignment(radioFileChooser, Alignment.TOP_LEFT);

        HorizontalLayout navigateButtons = createNavigateButtons(downloadButton, navigator);

        addComponent(label);
        addComponent(mainLayout);
        addComponent(navigateButtons);
        setComponentAlignment(label, Alignment.TOP_CENTER);
        setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);
        setComponentAlignment(navigateButtons, Alignment.BOTTOM_CENTER);
        setExpandRatio(mainLayout, 2);
        setExpandRatio(label, 1);
        setExpandRatio(navigateButtons, 1);
    }

    @NotNull
    private HorizontalLayout createNavigateButtons(Button downloadButton, Navigator navigator) {
        Button backButton = createBackButton(navigator);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSizeFull();
        buttons.setWidth(100, Unit.PERCENTAGE);
        buttons.addComponent(backButton);
        buttons.addComponent(downloadButton);
        buttons.setComponentAlignment(backButton, Alignment.BOTTOM_LEFT);
        buttons.setComponentAlignment(downloadButton, Alignment.BOTTOM_RIGHT);
        return buttons;
    }

    @NotNull
    private RadioButtonGroup<String> createRadioButtonGroup(MultipleMappingModel models, TextArea area) {
        RadioButtonGroup<String> radioFileChooser = new RadioButtonGroup<>("File format");
        radioFileChooser.setItems("Java", "XML");
        radioFileChooser.setValue("Java");
        radioFileChooser.addValueChangeListener(event -> {
            if (event.getValue().equals("Java")) {
                generator = new JavaFileMappingGenerator(models);
                fileName = "Mapping.java";
            } else {
                generator = new XmlFileMappingGenerator(models);
                fileName = "Mapping.xml";
            }
            generateMapping();
            area.setValue(generator.getGeneratedCode());
        });
        return radioFileChooser;
    }

    private void generateMapping() {
        try {
            generator.generate();
        } catch (Exception e) {
            BaseViewUtils.showErrorNotification("Mapping was failed", e.getMessage());
        }
    }

    @NotNull
    private TextArea createTextArea() {
        TextArea area = new TextArea();
        area.setValue(generator.getGeneratedCode());
        return area;
    }

    private Button createBackButton(Navigator navigator) {
        Button backButton = new Button("Back", event -> navigator.navigateTo(MainUI.MAPPING_VIEW));
        backButton.setWidth(10, Unit.PICAS);
        return backButton;
    }

    private InputStream getStream() {
        return new StringInputStream(generator.getGeneratedCode());
    }
}
