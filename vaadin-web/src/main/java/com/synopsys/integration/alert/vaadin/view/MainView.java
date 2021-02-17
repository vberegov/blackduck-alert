package com.synopsys.integration.alert.vaadin.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;

@Route("vaadin")
public class MainView extends AppLayout {
    public MainView() {
        addToNavbar(
            new H1("Hello World")
        );
    }
}
