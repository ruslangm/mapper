package ru.sbt.integration.orchestration.projects.view.utils;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

public class BaseViewUtils {
    public static void showErrorNotification(String caption, String description) {
        Notification notification = new Notification(caption, description, Notification.Type.ERROR_MESSAGE);
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.show(Page.getCurrent());
    }

    public static void showErrorNotification(String caption) {
        Notification notification = new Notification(caption, Notification.Type.ERROR_MESSAGE);
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.show(Page.getCurrent());
    }
}
