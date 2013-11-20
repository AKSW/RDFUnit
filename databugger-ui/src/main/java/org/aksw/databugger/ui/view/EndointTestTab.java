package org.aksw.databugger.ui.view;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.databugger.tests.TestExecutor;
import org.aksw.databugger.ui.components.SchemaSelectorComponent;
import org.aksw.databugger.ui.components.TestResultsComponent;

/**
 * User: Dimitris Kontokostas
 * This is a placeholder to keep the main content components
 * Created: 11/15/13 8:19 AM
 */

public class EndointTestTab extends VerticalLayout {

    private TestExecutor testExecutor = null;

    private final NativeSelect examplesSelect = new NativeSelect("Select an example");
    private final TextField endpointField = new TextField();
    private final TextField graphField = new TextField();
    private final SchemaSelectorComponent schemaSelectorWidget = new SchemaSelectorComponent();
    private final TestResultsComponent testResultsComponent = new TestResultsComponent();

    private final NativeSelect limitSelect = new NativeSelect();
    private final Button clearButton = new Button("Clear");
    private final Button generateTestsButton = new Button("Generate tests");
    private final Button startTestingButton = new Button("Run tests");
    private final Button resultsButton = new Button("Display Results");





    public EndointTestTab() {
        initLayout();


    }

    private void initLayout() {
        this.setMargin(true);
        this.setId("EndointTestTab");
        this.setWidth("100%");

        this.addComponent(new Label("<h2>Testing Configuration</h2>", ContentMode.HTML));

        HorizontalLayout configurationSetLayout = new HorizontalLayout();
        configurationSetLayout.setId("test-configuration");
        //configurationSetLayout.setWidth("100%");

        this.addComponent(configurationSetLayout);

        examplesSelect.addStyleName("examples");
        examplesSelect.addItem("DBpedia");
        examplesSelect.addItem("DBpedia in Dutch");
        examplesSelect.addItem("DBpedia Live");

        configurationSetLayout.addComponent(examplesSelect);

        HorizontalLayout manualConfigurationLayout = new HorizontalLayout();
        configurationSetLayout.addComponent(manualConfigurationLayout);
        configurationSetLayout.setExpandRatio(manualConfigurationLayout,1.0f);
        manualConfigurationLayout.addStyleName("manual");


        VerticalLayout textboxes = new VerticalLayout();
        manualConfigurationLayout.addComponent(textboxes);

        textboxes.addComponent(new Label("SPARQL Endpoint"));
        textboxes.addComponent(endpointField);
        textboxes.addComponent(new Label("Graph"));
        textboxes.addComponent(graphField);

        schemaSelectorWidget.addStyleName("schema-selector");
        manualConfigurationLayout.addComponent(schemaSelectorWidget);
        manualConfigurationLayout.setExpandRatio(schemaSelectorWidget,1.0f);

        HorizontalLayout hz = new HorizontalLayout();
        this.addComponent(hz);
        hz.addComponent(clearButton);
        hz.setComponentAlignment(clearButton, Alignment.MIDDLE_LEFT);
        hz.addComponent(generateTestsButton);
        hz.setComponentAlignment(generateTestsButton, Alignment.MIDDLE_RIGHT);


        this.addComponent(new Label("<h2>Test Generation</h2>", ContentMode.HTML));
        this.addComponent(this.startTestingButton);
        this.startTestingButton.setEnabled(false);

        this.addComponent(new Label("<h2>Testing</h2>", ContentMode.HTML));
        this.addComponent(this.testResultsComponent);
        this.addComponent(this.resultsButton);
        this.resultsButton.setEnabled(false);




    }
}