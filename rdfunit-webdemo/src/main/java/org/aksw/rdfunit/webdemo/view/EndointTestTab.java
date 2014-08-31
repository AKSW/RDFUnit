package org.aksw.rdfunit.webdemo.view;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.components.TestResultsComponent;
import org.aksw.rdfunit.webdemo.presenter.DataSelectorPresenter;
import org.aksw.rdfunit.webdemo.presenter.SchemaSelectorPresenter;
import org.aksw.rdfunit.webdemo.presenter.TestGenerationPresenter;

import java.io.File;

/**
 * @author Dimitris Kontokostas
 *         This is a placeholder to keep the main content components
 *         TODO everything is clattered in here need to separate many things
 * @since 11/15/13 8:19 AM
 */

public class EndointTestTab extends VerticalLayout {


//    private final NativeSelect examplesSelect = new NativeSelect("Select an example");
//    private final TextField endpointField = new TextField();
//    private final TextField graphField = new TextField();
//    private final SchemaSelectorComponent schemaSelectorWidget = new SchemaSelectorComponent();
    private final TestResultsComponent testResultsComponent = new TestResultsComponent();
    private final TestGenerationViewImpl testGenerationViewImpl = new TestGenerationViewImpl();

//    private final NativeSelect limitSelect = new NativeSelect();
    private final Button clearButton = new Button("Clear");

    private final Button startTestingButton = new Button("Run tests");
    private final Button startTestingCancelButton = new Button("Cancel");
    private final Button resultsButton = new Button("Display Results");

    private final ProgressBar testingProgress = new ProgressBar();
    private final Label testingProgressLabel = new Label("0/0");

    public EndointTestTab() {
        initLayout();

        //TODO move this away from here
//        File f = VaadinSession.getCurrent().getService().getBaseDirectory();
//        String baseDir = f.getAbsolutePath() + "/data/";
//        try {
//
//            RDFUnitConfiguration dbpediaConf = RDFunitConfigurationFactory.createDBpediaConfigurationSimple(baseDir);
//            RDFUnitConfiguration dbpediaLConf = RDFunitConfigurationFactory.createDBpediaLiveConfigurationSimple(baseDir);
//            RDFUnitConfiguration dbpediaNLConf = RDFunitConfigurationFactory.createDBpediaNLDatasetSimple(baseDir);
//            RDFUnitConfiguration linkedChemistry = RDFunitConfigurationFactory.createConfiguration("http://linkedchemistry.info", "http://rdf.farmbio.uu.se/chembl/sparql", Arrays.asList("http://linkedchemistry.info/chembl/"), "cheminf,cito", baseDir);
//            RDFUnitConfiguration uriBurner = RDFunitConfigurationFactory.createConfiguration("http://linkeddata.uriburner.com", "http://linkeddata.uriburner.com/sparql/", new ArrayList<String>(), "foaf,skos,geo,dcterms,prov", baseDir);
//            RDFUnitConfiguration bbcNature = RDFunitConfigurationFactory.createConfiguration("http://bbc.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://www.bbc.co.uk/nature/"), "dcterms,po,wo,wlo,foaf", baseDir);
//            RDFUnitConfiguration musicBrainz = RDFunitConfigurationFactory.createConfiguration("http://musicbrainz.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://www.bbc.co.uk/nature/"), "ov,mo,foaf", baseDir);
//            RDFUnitConfiguration umls = RDFunitConfigurationFactory.createConfiguration("http://umls.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://linkedlifedata.com/resource/umls"), "dcterms,skos,owl", baseDir);
//            RDFUnitConfiguration umbel = RDFunitConfigurationFactory.createConfiguration("http://umpel.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://umbel.org"), "vann,skos,owl", baseDir);
//            RDFUnitConfiguration datasw = RDFunitConfigurationFactory.createConfiguration("http://datasw.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://data.semanticweb.org"), "cal,event,tl,dcterms,bibo,rooms,cal,skos,foaf", baseDir);
//
//
//            examplesSelect.addItem(uriBurner);
//            examplesSelect.setItemCaption(uriBurner, "Uri Burner");
//            examplesSelect.addItem(bbcNature);
//            examplesSelect.setItemCaption(bbcNature, "BBC Nature (LOD Cache)");
//            examplesSelect.addItem(musicBrainz);
//            examplesSelect.setItemCaption(musicBrainz, "MusicBrainz (LOD Cache)");
//            examplesSelect.addItem(umls);
//            examplesSelect.setItemCaption(umls, "LinkedLifeData UMLS (LOD Cache)");
//            examplesSelect.addItem(umbel);
//            examplesSelect.setItemCaption(umbel, "umbel (LOD Cache)");
//            examplesSelect.addItem(datasw);
//            examplesSelect.setItemCaption(datasw, "data.semanticweb.org (LOD Cache)");
//            examplesSelect.addItem(linkedChemistry);
//            examplesSelect.setItemCaption(linkedChemistry, "LinkedChemistry");
//            examplesSelect.addItem(dbpediaConf);
//            examplesSelect.setItemCaption(dbpediaConf, "DBpedia");
//            examplesSelect.addItem(dbpediaLConf);
//            examplesSelect.setItemCaption(dbpediaLConf, "DBpedia Live");
//            examplesSelect.addItem(dbpediaNLConf);
//            examplesSelect.setItemCaption(dbpediaNLConf, "DBpedia NL");
//        } catch (UndefinedSchemaException e) {
//            //
//        }


        initInteractions();
    }

    private void initLayout() {
        this.setMargin(true);
        this.setId("EndointTestTab");
        this.setWidth("100%");

        this.addComponent( new Label("<h2>1. Data Selection</h2>", ContentMode.HTML));

        // Create the model and the Vaadin view implementation
        DataSelectorViewImpl dataSelectorView = new DataSelectorViewImpl();
        new DataSelectorPresenter(dataSelectorView);
        this.addComponent(dataSelectorView);

        this.addComponent( new Label("<h2>2. Constraints Selection</h2>", ContentMode.HTML));
        SchemaSelectorViewImpl schemaSelectorView = new SchemaSelectorViewImpl();
        new SchemaSelectorPresenter(schemaSelectorView);
        this.addComponent(schemaSelectorView);



//        HorizontalLayout confHeader = new HorizontalLayout();
//        this.addComponent(confHeader);
//
//        confHeader.addStyleName("header");
//        Label confLabel = new Label("<h2>Testing Configuration</h2>", ContentMode.HTML);
//        confHeader.addComponent(confLabel);
//        confHeader.setComponentAlignment(confLabel, Alignment.MIDDLE_LEFT);
//        confHeader.addComponent(clearButton);
//        confHeader.setComponentAlignment(clearButton, Alignment.MIDDLE_CENTER);
//
//
//        HorizontalLayout configurationSetLayout = new HorizontalLayout();
//        configurationSetLayout.setId("test-configuration");
//        //configurationSetLayout.setWidth("100%");
//
//        this.addComponent(configurationSetLayout);
//
//        examplesSelect.addStyleName("examples");
//        examplesSelect.setImmediate(true);
//
//        configurationSetLayout.addComponent(examplesSelect);
//
//        HorizontalLayout manualConfigurationLayout = new HorizontalLayout();
//        configurationSetLayout.addComponent(manualConfigurationLayout);
//        configurationSetLayout.setExpandRatio(manualConfigurationLayout, 1.0f);
//        manualConfigurationLayout.addStyleName("manual");
//
//
//        VerticalLayout textboxes = new VerticalLayout();
//        manualConfigurationLayout.addComponent(textboxes);
//
//        textboxes.addComponent(new Label("SPARQL Endpoint"));
//        textboxes.addComponent(endpointField);
//        textboxes.addComponent(new Label("Graph"));
//        textboxes.addComponent(graphField);
//
//        endpointField.setWidth("150px");
//        graphField.setWidth("150px");
//
//        schemaSelectorWidget.addStyleName("schema-selector");
//        manualConfigurationLayout.addComponent(schemaSelectorWidget);
//        manualConfigurationLayout.setExpandRatio(schemaSelectorWidget, 1.0f);
//
//
        this.addComponent( new Label("<h2>3. Test Generation</h2>", ContentMode.HTML));

        TestGenerationViewImpl testGenerationView = new TestGenerationViewImpl();
        new TestGenerationPresenter(testGenerationView);
        this.addComponent(testGenerationView);

        // Set previous / next
        dataSelectorView.setNextItem(schemaSelectorView);
        schemaSelectorView.setPreviousItem(dataSelectorView);
        schemaSelectorView.setNextItem(testGenerationView);



        this.addComponent( new Label("<h2>4. Testing</h2>", ContentMode.HTML));

        HorizontalLayout testHeader = new HorizontalLayout();
        this.addComponent(testHeader);
        testHeader.addStyleName("header");
        Label testLabel = new Label("<h2>Testing</h2>", ContentMode.HTML);
        testHeader.addComponent(testLabel);
        testHeader.setComponentAlignment(testLabel, Alignment.MIDDLE_LEFT);
        testHeader.addComponent(startTestingButton);
        testHeader.setComponentAlignment(startTestingButton, Alignment.MIDDLE_CENTER);
        testHeader.addComponent(testingProgress);
        testHeader.setComponentAlignment(testingProgress, Alignment.MIDDLE_CENTER);
        testHeader.addComponent(testingProgressLabel);
        testHeader.setComponentAlignment(testingProgressLabel, Alignment.MIDDLE_CENTER);
        testHeader.addComponent(startTestingCancelButton);
        testHeader.setComponentAlignment(startTestingCancelButton, Alignment.MIDDLE_CENTER);

        testingProgress.setWidth("150px");

        this.addComponent(this.testResultsComponent);
        //this.addComponent(this.resultsButton);
    }

    private void initInteractions() {
//        examplesSelect.addValueChangeListener(new Property.ValueChangeListener() {
//            @Override
//            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
//                Property property = valueChangeEvent.getProperty();
//                RDFUnitConfiguration configuration = (RDFUnitConfiguration) property.getValue();
//                if (configuration != null) {
//                    setExampleConfiguration(configuration);
//                    RDFUnitDemoSession.setRDFUnitConfiguration(configuration);
//                }
//            }
//        });

        clearButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                clearConfigurations();
            }
        });







        class TestExecutorThread extends Thread {
            @Override
            public void run() {

                //TODO make this cleaner
                if (RDFUnitDemoSession.getRDFUnitConfiguration() != null) {
                    Source dataset = RDFUnitDemoSession.getRDFUnitConfiguration().getTestSource();

                    RDFUnitDemoSession.getTestExecutor().addTestExecutorMonitor(testResultsComponent);
                    String resultsFile = RDFUnitDemoSession.getBaseDir() + "results/" + dataset.getPrefix() + ".results.ttl";
                    //TODO refactor this, do not use cache here
                    File f = new File(resultsFile);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                    RDFUnitDemoSession.getTestExecutor().execute(dataset, RDFUnitDemoSession.getTestSuite(), 3);
                }

            }
        }

        // Clicking the button creates and runs a work thread
        startTestingButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                startTestingButton.setEnabled(false);
                final TestExecutorThread thread = new TestExecutorThread();
                thread.start();

                UI.getCurrent().setPollInterval(1000);
            }
        });

        RDFUnitDemoSession.getTestExecutor().addTestExecutorMonitor(new TestExecutorMonitor() {
            private long count = 0;
            private long totalErrors = 0;
            private long failTest = 0;
            private long sucessTests = 0;
            private long timeoutTests = 0;
            private long total = 0;

            @Override
            public void testingStarted(final Source source, final TestSuite testSuite) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        count = totalErrors = failTest = sucessTests = timeoutTests = 0;
                        total = testSuite.size();

                        startTestingCancelButton.setEnabled(true);
                        testingProgress.setEnabled(true);
                        testingProgress.setValue(0.0f);
                        testingProgressLabel.setValue("0/" + total);

                    }
                });
            }

            @Override
            public void singleTestStarted(final TestCase test) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void singleTestExecuted(final TestCase test, final TestCaseResultStatus status, final java.util.Collection<TestCaseResult> results) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        long errors = 0;
                        TestCaseResult result = RDFUnitUtils.getFirstItemInCollection(results);
                        if (result != null && result instanceof AggregatedTestCaseResult) {
                            errors = ((AggregatedTestCaseResult) result).getErrorCount();
                        }
                        count++;
                        totalErrors += (errors > 0 ? errors : 0);
                        if (errors == -1)
                            timeoutTests++;
                        else {
                            if (errors == 0)
                                sucessTests++;
                            else
                                failTest++;
                        }

                        testingProgress.setValue((float) count / total);
                        testingProgressLabel.setValue(count + "/" + total + " (S: " + sucessTests + " / F: " + failTest + " / T: " + timeoutTests + " / E : " + totalErrors + ")");

                        if ((timeoutTests == 10 || timeoutTests == 30) && sucessTests == 0 && failTest == 0) {
                            //Too many timeouts maybe banned
                            Notification.show("Too many timeouts",
                                    "Maybe the endpoint banned this IP. Try a different endpoint of try the tool from a different IP.",
                                    Notification.Type.WARNING_MESSAGE);
                        }
                    }
                });
            }

            @Override
            public void testingFinished() {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        testingProgress.setValue(1.0f);
                        testingProgressLabel.setValue("Completed! (S: " + sucessTests + " / F:" + failTest + " / T: " + timeoutTests + " / E : " + totalErrors + ")");
                        startTestingCancelButton.setEnabled(false);
                        UI.getCurrent().setPollInterval(-1);
                    }
                });
            }
        });

        startTestingCancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                RDFUnitDemoSession.getTestExecutor().cancel();
            }
        });

    }

    private void clearConfigurations() {

//        endpointField.setValue("");
//        graphField.setValue("");
//        schemaSelectorWidget.setSelections(new ArrayList<SchemaSource>());
//        examplesSelect.select(null);

        testResultsComponent.clearTableRowsAndHide();
        testGenerationViewImpl.clearTableRowsAndHide();

        //generateTestsProgressLabel.setValue("0/0");
        testingProgressLabel.setValue("0/0");

    }

//    private void createConfigurationFromUser() {
//
//        String datasetURI = endpointField.getValue().replace("/sparql", "");
//
//        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetURI, RDFUnitDemoSession.getBaseDir());
//        configuration.setEndpointConfiguration(endpointField.getValue(), Arrays.asList(graphField.getValue()));
//        configuration.setSchemata(schemaSelectorWidget.getSelections());
//
//        RDFUnitDemoSession.setRDFUnitConfiguration(configuration);
//    }

//    private void setExampleConfiguration(RDFUnitConfiguration configuration) {
//        if (!(endpointField.getValue().isEmpty() || graphField.getValue().isEmpty() || schemaSelectorWidget.getSelections().isEmpty())) {
//            //TODO confirm dialog for clear
//            clearConfigurations();
//        }
//
//        Source dataset = configuration.getTestSource();
//        if (dataset instanceof EndpointTestSource) {
//            endpointField.setValue(((EndpointTestSource) dataset).getSparqlEndpoint());
//            java.util.Collection<String> graphs = ((EndpointTestSource) dataset).getSparqlGraphs();
//            String graph = RDFUnitUtils.getFirstItemInCollection(graphs);
//            if (graph != null && !graph.isEmpty()) {
//                graphField.setValue(graph);
//            }
//        } else {
//            endpointField.setValue("");
//            graphField.setValue("");
//        }
//        schemaSelectorWidget.setSelections(dataset.getReferencesSchemata());
//    }
}