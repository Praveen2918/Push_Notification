package com.example.application.views.helloworld;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.concurrent.CompletableFuture;

import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

import com.example.service.BrowserNotifications;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
@Route(value = "hello", layout = MainView.class)
@PageTitle("Hello World")
@CssImport("./styles/views/helloworld/hello-world-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class HelloWorldView extends VerticalLayout {

	private final BrowserNotifications notifications;
    public HelloWorldView() {
    	UI ui = getUI().orElse(UI.getCurrent());
        notifications = BrowserNotifications.extend(ui);

        notifications.queryIfSupported().thenAccept(this::acceptSupportState)
                .thenCompose(aVoid -> notifications.queryPermissionLevel())
                .thenCompose(this::acceptPermissionLevel)
                .thenCompose(event -> notifications.askForPermission())
                .thenAccept(this::conditionallyShowNotifierButton);
    }
    private CompletableFuture<ClickEvent<Button>> acceptPermissionLevel(
            BrowserNotifications.PermissionLevel permissionLevel) {
        CompletableFuture<ClickEvent<Button>> clickPromise = new CompletableFuture<>();
        add(new Span("Permission level is now: " + permissionLevel.name()));
        if (permissionLevel == BrowserNotifications.PermissionLevel.DEFAULT
                || permissionLevel == BrowserNotifications.PermissionLevel.GRANTED) {
            add(new Button("Click here to receive notificiations from this website",
                    (ComponentEventListener<ClickEvent<Button>>) event -> clickPromise
                            .complete(event)));
        }
        return clickPromise;
    }

    private void acceptSupportState(Boolean isSupported) {
        add(new Span(isSupported ?
                "Notifications are supported in this browser" :
                "Notifications are not supported in this browser…"));
    }

    private void conditionallyShowNotifierButton(
            BrowserNotifications.PermissionLevel permissionLevel) {
        if (permissionLevel == BrowserNotifications.PermissionLevel.GRANTED) {

            Button button = new Button("Click here to be notified immediately",
                    (ComponentEventListener<ClickEvent<Button>>) event -> {
                        notifications.showNotification("You're now being notified.");
                        System.out.println("Sending notification");
                    });
            add(button);
        } else {
            add(new Span("Unfortunately, user agent doesn't allow notifications"));
        }
        return;
    }
}
