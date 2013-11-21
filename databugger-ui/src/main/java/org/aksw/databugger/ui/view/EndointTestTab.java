package org.aksw.databugger.ui.view;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.databugger.Databugger;
import org.aksw.databugger.DatabuggerConfiguration;
import org.aksw.databugger.DatabuggerConfigurationFactory;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.tests.TestExecutor;
import org.aksw.databugger.tests.TestGeneratorExecutor;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.databugger.ui.DatabuggerUISession;
import org.aksw.databugger.ui.components.SchemaSelectorComponent;
import org.aksw.databugger.ui.components.TestGenerationComponent;
import org.aksw.databugger.ui.components.TestResultsComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * This is a placeholder to keep the main content components
 * Created: 11/15/13 8:19 AM
 */

public class EndointTestTab extends VerticalLayout {

    private TestExecutor testExecutor = null;
    private final List<UnitTest> tests = new ArrayList<UnitTest>();

    private final NativeSelect examplesSelect = new NativeSelect("Select an example");
    private final TextField endpointField = new TextField();
    private final TextField graphField = new TextField();
    private final SchemaSelectorComponent schemaSelectorWidget = new SchemaSelectorComponent();
    private final TestResultsComponent testResultsComponent = new TestResultsComponent();
    private final TestGenerationComponent testGenerationComponent = new TestGenerationComponent();

    private final NativeSelect limitSelect = new NativeSelect();
    private final Button clearButton = new Button("Clear");
    private final Button generateTestsButton = new Button("Generate tests");
    private final Button startTestingButton = new Button("Run tests");
    private final Button resultsButton = new Button("Display Results");

    public EndointTestTab() {
        initLayout();

        //TODO move this away from here
        File f = VaadinSession.getCurrent().getService().getBaseDirectory();
        String baseDir = f.getAbsolutePath()+"/data/";

        DatabuggerConfiguration dbpediaConf = DatabuggerConfigurationFactory.createDBpediaConfigurationSimple(baseDir);
        DatabuggerConfiguration dbpediaLConf = DatabuggerConfigurationFactory.createDBpediaLiveConfigurationSimple(baseDir);
        DatabuggerConfiguration dbpediaNLConf = DatabuggerConfigurationFactory.createDBpediaNLDatasetSimple(baseDir);

        examplesSelect.addItem(dbpediaConf);
        examplesSelect.setItemCaption(dbpediaConf,"DBpedia");
        examplesSelect.addItem(dbpediaLConf);
        examplesSelect.setItemCaption(dbpediaLConf,"DBpedia Live");
        examplesSelect.addItem(dbpediaNLConf);
        examplesSelect.setItemCaption(dbpediaNLConf,"DBpedia NL");

        initInteractions();
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
        examplesSelect.setImmediate(true);

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

        endpointField.setWidth("150px");
        graphField.setWidth("150px");

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
        this.addComponent(testGenerationComponent);
        this.addComponent(this.startTestingButton);
        this.startTestingButton.setEnabled(false);

        this.addComponent(new Label("<h2>Testing</h2>", ContentMode.HTML));
        this.addComponent(this.testResultsComponent);
        this.addComponent(this.resultsButton);
        this.resultsButton.setEnabled(false);

    }

    private void initInteractions(){
        examplesSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                Property property = valueChangeEvent.getProperty();
                DatabuggerConfiguration configuration = (DatabuggerConfiguration) property.getValue();
                if (configuration != null) {
                    setExampleConfiguration(configuration);
                }
            }
        });

        clearButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                clearConfigurations();
            }
        });



        class TestGenerationThread extends Thread {
            // Volatile because read in another thread in access()
            volatile double current = 0.0;

            @Override
            public void run() {

                EndointTestTab.this.tests.clear();
                Databugger databugger = DatabuggerUISession.getDatabugger();
                TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
                testGeneratorExecutor.addTestExecutorMonitor(testGenerationComponent);
                DatabuggerConfiguration configuration = getCurrentConfiguration();

                EndointTestTab.this.tests.addAll(
                        testGeneratorExecutor.generateTests(
                                DatabuggerUISession.getBaseDir()+"tests/",
                                configuration.getDatasetSource(),
                                databugger.getAutoGenerators()));

                UI.getCurrent().setPollInterval(-1);

            }
        }

        // Clicking the button creates and runs a work thread
        generateTestsButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                final TestGenerationThread thread = new TestGenerationThread();
                thread.start();

                // Enable polling and set frequency to 0.5 seconds
                UI.getCurrent().setPollInterval(500);
            }
        });

    }

    private void clearConfigurations(){

        endpointField.setValue("");
        graphField.setValue("");
        schemaSelectorWidget.setSelections(new ArrayList<SchemaSource>());
        examplesSelect.select(null);
    }

    private DatabuggerConfiguration getCurrentConfiguration(){
        return new DatabuggerConfiguration(endpointField.getValue().replace("/sparql",""),endpointField.getValue(), graphField.getValue(), schemaSelectorWidget.getSelections());
    }

    private void setExampleConfiguration(DatabuggerConfiguration configuration){
        if (!(endpointField.getValue().isEmpty() || graphField.getValue().isEmpty() || schemaSelectorWidget.getSelections().isEmpty())) {
            //TODO confirm dialog for clear
            clearConfigurations();
        }

        DatasetSource dataset = configuration.getDatasetSource();
        endpointField.setValue(dataset.getSparqlEndpoint());
        graphField.setValue(dataset.getSparqlGraph());
        schemaSelectorWidget.setSelections(dataset.getReferencesSchemata());
    }
}