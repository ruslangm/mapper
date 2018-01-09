package ru.sbt.integration.orchestration.projects.view;

import com.vaadin.event.selection.MultiSelectionEvent;

import java.util.ArrayList;
import java.util.List;

public class ChooseViewController {
    private final List<Class<?>> sourceClasses = new ArrayList<>();
    private Class<?> destinationClass;

    public List<Class<?>> getSourceClasses() {
        return sourceClasses;
    }

    public Class<?> getDestinationClass() {
        return destinationClass;
    }

    public void setSourceItemSelected(MultiSelectionEvent<Class<?>> event) {
        sourceClasses.clear();
        sourceClasses.addAll(event.getAllSelectedItems());
    }

    public void setDestinationItemSelected(MultiSelectionEvent<Class<?>> event) {
        destinationClass = event.getFirstSelectedItem().orElse(null);
    }
}
