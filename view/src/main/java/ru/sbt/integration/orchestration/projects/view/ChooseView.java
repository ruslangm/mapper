package ru.sbt.integration.orchestration.projects.view;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import org.jetbrains.annotations.NotNull;
import ru.sbt.integration.orchestration.projects.classloader.ArtifactClassLoader;
import ru.sbt.integration.orchestration.projects.classloader.ArtifactClassLoaderImpl;
import ru.sbt.integration.orchestration.projects.dependency.MavenArtifact;
import ru.sbt.integration.orchestration.projects.view.utils.BaseViewUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sbt-shipov-ev on 21.09.2017.
 */
public class ChooseView extends VerticalLayout implements View {
    private final ChooseViewController viewController = new ChooseViewController();

    ChooseView(Navigator navigator, MavenArtifact source, MavenArtifact destination, List<String> repos) {
        setSizeFull();

        Image label = getLabel();
        label.setWidth(40, Unit.PERCENTAGE);

        HorizontalLayout listsLayout = createListsLayout(source, destination, repos);
        listsLayout.setWidth(70, Unit.PERCENTAGE);
        HorizontalLayout navigateButtons = createNavigateButtons(navigator);

        addComponent(label);
        addComponent(listsLayout);
        addComponent(navigateButtons);
        setComponentAlignment(label, Alignment.TOP_CENTER);
        setComponentAlignment(listsLayout, Alignment.MIDDLE_CENTER);
        setComponentAlignment(navigateButtons, Alignment.BOTTOM_CENTER);
    }

    private HorizontalLayout createListsLayout(MavenArtifact source, MavenArtifact destination, List<String> repos) {
        HorizontalLayout layout = new HorizontalLayout();
        ArtifactClassLoader loader = new ArtifactClassLoaderImpl();
        ListSelect<Class<?>> sourceList = new ListSelect<>("Select source class");
        sourceList.setWidth(100, Unit.PERCENTAGE);
        ListSelect<Class<?>> destinationList = new ListSelect<>("Select destination class");
        destinationList.setWidth(100, Unit.PERCENTAGE);

        try {
            sourceList.setItems(loader.getClasses(source, repos));
            destinationList.setItems(loader.getClasses(destination, repos));
        } catch (IOException e) {
            e.printStackTrace();
        }

        layout.addComponent(sourceList, 0);
        layout.addComponent(destinationList, 1);
        layout.setComponentAlignment(sourceList, Alignment.MIDDLE_RIGHT);
        layout.setComponentAlignment(destinationList, Alignment.MIDDLE_LEFT);
        sourceList.addSelectionListener(viewController::setSourceItemSelected);
        destinationList.addSelectionListener(viewController::setDestinationItemSelected);

        return layout;
    }

    @NotNull
    private HorizontalLayout createNavigateButtons(Navigator navigator) {
        Button backButton = createBackButton(navigator);
        Button nextButton = createNextButton(navigator);

        HorizontalLayout nextButtonLayout = new HorizontalLayout();
        nextButtonLayout.setWidth(100, Unit.PERCENTAGE);
        nextButtonLayout.addComponent(nextButton);
        nextButtonLayout.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

        HorizontalLayout previousButtonLayout = new HorizontalLayout();
        previousButtonLayout.setWidth(100, Unit.PERCENTAGE);
        previousButtonLayout.addComponent(backButton);
        previousButtonLayout.setComponentAlignment(backButton, Alignment.MIDDLE_LEFT);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeUndefined();
        footer.setSizeFull();
        footer.addComponent(previousButtonLayout);
        footer.setComponentAlignment(previousButtonLayout, Alignment.BOTTOM_LEFT);
        footer.addComponent(nextButtonLayout);
        footer.setComponentAlignment(nextButtonLayout, Alignment.BOTTOM_RIGHT);
        footer.setStyleName("custom-margin");
        return footer;
    }

    @NotNull
    private Button createBackButton(Navigator navigator) {
        Button backButton = new Button("Back", event -> navigator.navigateTo(MainUI.TITLE_VIEW));
        backButton.setWidth(12, Unit.PICAS);
        return backButton;
    }

    @NotNull
    private Button createNextButton(Navigator navigator) {
        Button nextWindowButton = new Button("Next", event -> {
            if (viewController.getSourceClasses() == null || viewController.getDestinationClass() == null) {
                BaseViewUtils.showErrorNotification("Please, choose classes");
            } else {
                navigator.addView(MainUI.MAPPING_VIEW, new MappingView(
                        navigator,
                        viewController.getSourceClasses(),
                        viewController.getDestinationClass()
                ));
                navigator.navigateTo(MainUI.MAPPING_VIEW);
            }
        });
        nextWindowButton.setWidth(12, Unit.PICAS);
        return nextWindowButton;
    }

    @NotNull
    private Image getLabel() {
        String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        return new Image("", new FileResource(new File(basePath +
                "/resources/orc-logo.png")));
    }
}
