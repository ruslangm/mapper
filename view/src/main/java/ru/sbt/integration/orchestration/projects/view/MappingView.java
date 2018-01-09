package ru.sbt.integration.orchestration.projects.view;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import org.jetbrains.annotations.NotNull;
import ru.sbt.integration.orchestration.mapper.model.Node;

import java.lang.reflect.Field;
import java.util.List;

public class MappingView extends VerticalLayout implements View {
    private final MappingViewController mappingViewController = new MappingViewController();

    MappingView(Navigator navigator, List<Class<?>> sources, Class<?> destination) {
        setSizeFull();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        mappingViewController.generateModel(sources, destination);
        initTree(horizontalLayout);

        TextArea area = new TextArea();
        area.setSizeFull();
        area.setEnabled(false);
        area.setCaption("<h2>Mapping table</h2>");
        area.setCaptionAsHtml(true);

        Button buttonAddMapping = createButton("Add mapping",
                event -> mappingViewController.configureAddMappingButton(area));
        Button buttonClearMapping = createButton("Clear mapping history",
                event -> mappingViewController.configureClearMappingButton(area));

        VerticalLayout mappingLayout = new VerticalLayout();
        mappingLayout.setSizeFull();
        mappingLayout.addComponent(area);
        mappingLayout.setExpandRatio(area, 0.8f);
        mappingLayout.addComponent(buttonAddMapping);
        mappingLayout.setComponentAlignment(buttonAddMapping, Alignment.BOTTOM_CENTER);
        mappingLayout.setExpandRatio(buttonAddMapping, 0.1f);
        mappingLayout.addComponent(buttonClearMapping);
        mappingLayout.setComponentAlignment(buttonClearMapping, Alignment.BOTTOM_CENTER);
        mappingLayout.setExpandRatio(buttonClearMapping, 0.1f);
        horizontalLayout.addComponent(mappingLayout);
        addComponent(horizontalLayout);
        setExpandRatio(horizontalLayout, 0.9f);
        setComponentAlignment(horizontalLayout, Alignment.MIDDLE_CENTER);

        HorizontalLayout buttons = createNavigateButtons(navigator, area);
        addComponent(buttons);
        setExpandRatio(buttons, 0.1f);
        setComponentAlignment(buttons, Alignment.BOTTOM_CENTER);
    }

    private void navigateToNextView(Navigator navigator) {
        if (!mappingViewController.isMappingFailed()) {
            navigator.addView(MainUI.GENERATOR_VIEW, new GeneratedMappingView(mappingViewController.getModel(), navigator));
            navigator.navigateTo(MainUI.GENERATOR_VIEW);
        }
    }

    @NotNull
    private Button createButton(String text, Button.ClickListener listener) {
        Button button = new Button(text, listener);
        button.setWidth(12, Unit.PICAS);
        return button;
    }

    private void initTree(HorizontalLayout layout) {
        VerticalLayout sourceLayout = new VerticalLayout();
        sourceLayout.setSizeFull();
        VerticalLayout destinationLayout = new VerticalLayout();
        destinationLayout.setSizeFull();

        Grid<Node<Field>> sourceGrid = new TreeGrid<>();
        sourceGrid.setCaption("<h2>Source<h2>");
        sourceGrid.setCaptionAsHtml(true);

        Grid<Node<Field>> destinationGrid = new TreeGrid<>();
        destinationGrid.setSizeFull();
        destinationGrid.setCaption("<h2>Destination<h2>");
        destinationGrid.setCaptionAsHtml(true);

        mappingViewController.configureSourceGrid(sourceGrid, sourceLayout);
        mappingViewController.configureDestinationGrid(destinationGrid, destinationLayout);

        sourceGrid.setStyleGenerator(node -> node.getter() == null ? "v-grid-row-no-method" : null);
        destinationGrid.setStyleGenerator(node -> node.setter() == null ? "v-grid-row-no-method" : null);

        sourceGrid.addItemClickListener(mappingViewController::setSourceItemClickListener);
        destinationGrid.addItemClickListener(mappingViewController::setDestinationItemClickListener);

        layout.addComponent(sourceLayout);
        layout.addComponent(destinationLayout);
    }

    private HorizontalLayout createNavigateButtons(Navigator navigator, TextArea area) {
        HorizontalLayout buttons = new HorizontalLayout();
        Button nextButton = createButton("Generate mapping",
                event -> {
                    mappingViewController.configureGenerateMappingButton(area);
                    navigateToNextView(navigator);
                });
        Button backButton = createButton("Back", event -> navigator.navigateTo(MainUI.CHOOSE_VIEW));
        buttons.setWidth(100, Unit.PERCENTAGE);
        buttons.addComponent(backButton);
        buttons.addComponent(nextButton);
        buttons.setComponentAlignment(backButton, Alignment.BOTTOM_LEFT);
        buttons.setComponentAlignment(nextButton, Alignment.BOTTOM_RIGHT);

        return buttons;
    }
}