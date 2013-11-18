package org.aksw.databugger.ui.view;

import com.vaadin.ui.*;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.ui.widgets.SchemaSelectorWidget;
import org.vaadin.tokenfield.TokenField;

/**
 * User: Dimitris Kontokostas
 * This is a placeholder to keep the main content components
 * Created: 11/15/13 8:19 AM
 */

public class EndointTestTab extends VerticalLayout {

    private final NativeSelect examples = new NativeSelect("Select an example");
    private final TextField endpoint = new TextField();
    private final TextField graph = new TextField();
    private final TokenField vocabularies = new TokenField("Vocabularies");


    public EndointTestTab() {
        initLayout();
    }

    private void initLayout() {
        this.setMargin(true);
        this.setId("EndointTestTab");

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        this.addComponent(horizontalLayout);

        examples.addStyleName("col-1");
        examples.addItem("DBpedia");
        examples.addItem("DBpedia in Dutch");
        examples.addItem("DBpedia Live");

        horizontalLayout.addComponent(examples);

        VerticalLayout textboxes = new VerticalLayout();
        textboxes.addStyleName("col-2");
        horizontalLayout.addComponent(textboxes);

        textboxes.addComponent(new Label("SPARQL Endpoint"));
        textboxes.addComponent(endpoint);
        textboxes.addComponent(new Label("Graph"));
        textboxes.addComponent(graph);

        vocabularies.addStyleName("col-3");
        horizontalLayout.addComponent(new SchemaSelectorWidget());


    }
}