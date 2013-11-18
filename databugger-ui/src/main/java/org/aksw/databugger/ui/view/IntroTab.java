package org.aksw.databugger.ui.view;

import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

/**
 * User: Dimitris Kontokostas
 * displayes general information about the tool
 * Created: 11/15/13 7:41 PM
 */
public class IntroTab  extends VerticalLayout {

    public IntroTab() {
        initLayout();

    }

    private void initLayout() {
        this.setMargin(true);
        this.addComponent( new Label("bla bla bla"));

    }
}
