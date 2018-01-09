package ru.sbt.integration.orchestration.projects.view;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import org.jetbrains.annotations.NotNull;
import ru.sbt.integration.orchestration.projects.dependency.MavenArtifact;
import ru.sbt.integration.orchestration.projects.dependency.exception.DependencyLoaderException;
import ru.sbt.integration.orchestration.projects.view.utils.BaseViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleView extends VerticalLayout implements View {
    private final TitleViewController viewController = new TitleViewController();
    private final TextArea repositoriesArea = new TextArea();
    private final FormLayout sourceForm = new FormLayout();
    private final FormLayout destinationForm = new FormLayout();

    TitleView(Navigator navigator) {
        setSizeFull();

        Image label = getLabel();
        label.setWidth(45, Unit.PERCENTAGE);

        HorizontalLayout mainLayout = createFormsLayout();
        mainLayout.setWidth(100, Unit.PERCENTAGE);
        HorizontalLayout navigateButtons = createNavigateButtons(navigator);

        configureRepositoriesArea(repositoriesArea);

        addComponent(label, 0);
        addComponent(mainLayout, 1);
        addComponent(repositoriesArea, 2);
        addComponent(navigateButtons, 3);
        setComponentAlignment(label, Alignment.TOP_CENTER);
        setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);
        setComponentAlignment(repositoriesArea, Alignment.BOTTOM_CENTER);
        setExpandRatio(label, 0.2f);
        setExpandRatio(mainLayout, 0.3f);
        setExpandRatio(repositoriesArea, 0.3f);
        setExpandRatio(navigateButtons, 0.2f);
    }

    private HorizontalLayout createFormsLayout() {
        HorizontalLayout mainLayout = new HorizontalLayout();
        Panel sourcePanel = new Panel("Source artifact");
        Panel destinationPanel = new Panel("Destination artifact");

        HorizontalLayout sourceLayout = new HorizontalLayout();
        sourceLayout.setWidth(100, Unit.PERCENTAGE);
        sourcePanel.setWidth(45, Unit.PERCENTAGE);
        sourceLayout.setMargin(true);
        configureForm(sourceForm);
        sourceLayout.addComponent(sourceForm);
        sourcePanel.setContent(sourceLayout);

        HorizontalLayout destinationLayout = new HorizontalLayout();
        destinationLayout.setMargin(true);
        destinationLayout.setWidth(100, Unit.PERCENTAGE);
        destinationPanel.setWidth(45, Unit.PERCENTAGE);
        configureForm(destinationForm);
        destinationLayout.addComponent(destinationForm);
        destinationPanel.setContent(destinationLayout);

        mainLayout.addComponent(sourcePanel);
        mainLayout.addComponent(destinationPanel);
        mainLayout.setComponentAlignment(sourcePanel, Alignment.MIDDLE_RIGHT);
        mainLayout.setComponentAlignment(destinationPanel, Alignment.MIDDLE_LEFT);

        return mainLayout;
    }

    @NotNull
    private HorizontalLayout createNavigateButtons(Navigator navigator) {
        Button backButton = createNextWindowButton(navigator);

        HorizontalLayout nextButtonLayout = new HorizontalLayout();
        nextButtonLayout.setWidth(100 , Unit.PERCENTAGE);
        nextButtonLayout.addComponent(backButton);
        nextButtonLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSizeFull();
        footer.addComponent(nextButtonLayout);
        footer.setComponentAlignment(nextButtonLayout, Alignment.BOTTOM_RIGHT);
        footer.setStyleName("custom-margin");
        return footer;
    }

    @NotNull
    private Button createNextWindowButton(Navigator navigator) {
        Button nextWindowButton = new Button("Next", event -> {
            if (isFormsFull()) {
                try {
                    getArtifacts();
                    navigator.addView(MainUI.CHOOSE_VIEW, new ChooseView(
                            navigator,
                            viewController.getSourceArtifact(),
                            viewController.getDestinationArtifact(),
                            viewController.getRepos()));
                    navigator.navigateTo(MainUI.CHOOSE_VIEW);
                } catch (DependencyLoaderException e) {
                    BaseViewUtils.showErrorNotification("Resolve dependencies error:", e.getMessage());
                }
            }
        });
        nextWindowButton.setWidth(10, Unit.PICAS);
        addComponent(nextWindowButton);
        return nextWindowButton;
    }

    @NotNull
    private Image getLabel() {
        String basePath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        return new Image("", new FileResource(new File(basePath +
                "/resources/orc-logo.png")));
    }

    private void getArtifacts() {
        MavenArtifact sourceArtifact = getArtifact(sourceForm);
        MavenArtifact destinationArtifact = getArtifact(destinationForm);
        viewController.setSourceArtifact(sourceArtifact);
        viewController.setDestinationArtifact(destinationArtifact);
        viewController.setRepos(getRepoList());
    }

    private MavenArtifact getArtifact(FormLayout form) {
        String groupID = ((TextField) form.getComponent(0)).getValue();
        String artifactID = ((TextField) form.getComponent(1)).getValue();
        String version = ((TextField) form.getComponent(2)).getValue();
        return new MavenArtifact(groupID, artifactID, version);
    }

    private boolean isFormsFull() {
        for (int i = 0; i < 3; i++) {
            if (((TextField) sourceForm.getComponent(i)).getValue().isEmpty() ||
                    ((TextField) destinationForm.getComponent(i)).getValue().isEmpty()) {
                BaseViewUtils.showErrorNotification("Please, fill the data");
                return false;
            }
        }
        if (repositoriesArea.getValue().isEmpty()) {
            BaseViewUtils.showErrorNotification("Please, add repository");
            return false;
        }
        return true;
    }

    private void configureForm(FormLayout form) {
        form.setMargin(false);
        TextField groupID = new TextField("Group ID", "com.github.cwilper.fcrepo-misc");
        TextField artifactID = new TextField("Artifact ID", "fcrepo-dto-core");
        TextField version = new TextField("Version", "1.0.0");
        groupID.setWidth(100, Unit.PERCENTAGE);
        artifactID.setWidth(100, Unit.PERCENTAGE);
        version.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(groupID, 0);
        form.addComponent(artifactID, 1);
        form.addComponent(version, 2);
    }

    private void configureRepositoriesArea(TextArea area) {
        area.setCaption("Repositories");
        area.setWidth(45, Unit.PERCENTAGE);
        area.setHeight(80, Unit.PERCENTAGE);
        area.setValue("http://central.maven.org/maven2/");
    }

    private List<String> getRepoList() {
        return new ArrayList<>(Arrays.asList(repositoriesArea.getValue().split("\\n")));
    }
}
