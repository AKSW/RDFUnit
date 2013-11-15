package org.aksw.databugger.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * User: Dimitris Kontokostas
 * This is a placeholder to keep the main content components
 * Created: 11/15/13 8:19 AM
 */
@SuppressWarnings("serial")
public class EndointTestMainView extends VerticalLayout implements View {

    public EndointTestMainView() {
        this.addComponent(new Label("Endpoint test"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {}
}