package org.aksw.rdfunit.ui.view;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.sources.DatasetSource;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.monitors.TestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.monitors.TestGeneratorExecutorMonitor;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;
import org.aksw.rdfunit.ui.RDFUnitUISession;
import org.aksw.rdfunit.ui.RDFunitConfigurationFactory;
import org.aksw.rdfunit.ui.components.SchemaSelectorComponent;
import org.aksw.rdfunit.ui.components.TestGenerationComponent;
import org.aksw.rdfunit.ui.components.TestResultsComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * This is a placeholder to keep the main content components
 * TODO everything is clattered in here need to separate many things
 * Created: 11/15/13 8:19 AM
 */

public class EndointTestTab extends VerticalLayout {


    private final NativeSelect examplesSelect = new NativeSelect("Select an example");
    private final TextField endpointField = new TextField();
    private final TextField graphField = new TextField();
    private final SchemaSelectorComponent schemaSelectorWidget = new SchemaSelectorComponent();
    private final TestResultsComponent testResultsComponent = new TestResultsComponent();
    private final TestGenerationComponent testGenerationComponent = new TestGenerationComponent();

    private final NativeSelect limitSelect = new NativeSelect();
    private final Button clearButton = new Button("Clear");
    private final Button generateTestsButton = new Button("Generate tests");
    private final Button generateTestsCancelButton = new Button("Cancel");
    private final Button startTestingButton = new Button("Run tests");
    private final Button startTestingCancelButton = new Button("Cancel");
    private final Button resultsButton = new Button("Display Results");

    private final ProgressBar generateTestsProgress = new ProgressBar();
    private final ProgressBar testingProgress = new ProgressBar();
    private final Label generateTestsProgressLabel = new Label("0/0");
    private final Label testingProgressLabel = new Label("0/0");

    public EndointTestTab() {
        initLayout();

        //TODO move this away from here
        File f = VaadinSession.getCurrent().getService().getBaseDirectory();
        String baseDir = f.getAbsolutePath() + "/data/";
        try {

            RDFUnitConfiguration dbpediaConf = RDFunitConfigurationFactory.createDBpediaConfigurationSimple(baseDir);
            RDFUnitConfiguration dbpediaLConf = RDFunitConfigurationFactory.createDBpediaLiveConfigurationSimple(baseDir);
            RDFUnitConfiguration dbpediaNLConf = RDFunitConfigurationFactory.createDBpediaNLDatasetSimple(baseDir);
            RDFUnitConfiguration linkedChemistry = RDFunitConfigurationFactory.createConfiguration("http://linkedchemistry.info", "http://rdf.farmbio.uu.se/chembl/sparql", Arrays.asList("http://linkedchemistry.info/chembl/"), "cheminf,cito", baseDir);
            RDFUnitConfiguration uriBurner = RDFunitConfigurationFactory.createConfiguration("http://linkeddata.uriburner.com", "http://linkeddata.uriburner.com/sparql/", new ArrayList<String>(), "foaf,skos,geo,dcterms,prov", baseDir);
            RDFUnitConfiguration bbcNature = RDFunitConfigurationFactory.createConfiguration("http://bbc.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://www.bbc.co.uk/nature/"), "dcterms,po,wo,wlo,foaf", baseDir);
            RDFUnitConfiguration musicBrainz = RDFunitConfigurationFactory.createConfiguration("http://musicbrainz.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://www.bbc.co.uk/nature/"), "ov,mo,foaf", baseDir);
            RDFUnitConfiguration umls = RDFunitConfigurationFactory.createConfiguration("http://umls.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://linkedlifedata.com/resource/umls"), "dcterms,skos,owl", baseDir);
            RDFUnitConfiguration umbel = RDFunitConfigurationFactory.createConfiguration("http://umpel.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://umbel.org"), "vann,skos,owl", baseDir);
            RDFUnitConfiguration datasw = RDFunitConfigurationFactory.createConfiguration("http://datasw.lod.openlinksw.com", "http://lod.openlinksw.com/sparql", Arrays.asList("http://data.semanticweb.org"), "cal,event,tl,dcterms,bibo,rooms,cal,skos,foaf", baseDir);


            examplesSelect.addItem(uriBurner);
            examplesSelect.setItemCaption(uriBurner, "Uri Burner");
            examplesSelect.addItem(bbcNature);
            examplesSelect.setItemCaption(bbcNature, "BBC Nature (LOD Cache)");
            examplesSelect.addItem(musicBrainz);
            examplesSelect.setItemCaption(musicBrainz, "MusicBrainz (LOD Cache)");
            examplesSelect.addItem(umls);
            examplesSelect.setItemCaption(umls, "LinkedLifeData UMLS (LOD Cache)");
            examplesSelect.addItem(umbel);
            examplesSelect.setItemCaption(umbel, "umbel (LOD Cache)");
            examplesSelect.addItem(datasw);
            examplesSelect.setItemCaption(datasw, "data.semanticweb.org (LOD Cache)");
            examplesSelect.addItem(linkedChemistry);
            examplesSelect.setItemCaption(linkedChemistry, "LinkedChemistry");
            examplesSelect.addItem(dbpediaConf);
            examplesSelect.setItemCaption(dbpediaConf, "DBpedia");
            examplesSelect.addItem(dbpediaLConf);
            examplesSelect.setItemCaption(dbpediaLConf, "DBpedia Live");
            examplesSelect.addItem(dbpediaNLConf);
            examplesSelect.setItemCaption(dbpediaNLConf, "DBpedia NL");
        } catch (UndefinedSchemaException e) {
            //
        }


        initInteractions();
    }

    private void initLayout() {
        this.setMargin(true);
        this.setId("EndointTestTab");
        this.setWidth("100%");

        HorizontalLayout confHeader = new HorizontalLayout();
        this.addComponent(confHeader);

        confHeader.addStyleName("header");
        Label confLabel = new Label("<h2>Testing Configuration</h2>", ContentMode.HTML);
        confHeader.addComponent(confLabel);
        confHeader.setComponentAlignment(confLabel, Alignment.MIDDLE_LEFT);
        confHeader.addComponent(clearButton);
        confHeader.setComponentAlignment(clearButton, Alignment.MIDDLE_CENTER);


        HorizontalLayout configurationSetLayout = new HorizontalLayout();
        configurationSetLayout.setId("test-configuration");
        //configurationSetLayout.setWidth("100%");

        this.addComponent(configurationSetLayout);

        examplesSelect.addStyleName("examples");
        examplesSelect.setImmediate(true);

        configurationSetLayout.addComponent(examplesSelect);

        HorizontalLayout manualConfigurationLayout = new HorizontalLayout();
        configurationSetLayout.addComponent(manualConfigurationLayout);
        configurationSetLayout.setExpandRatio(manualConfigurationLayout, 1.0f);
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
        manualConfigurationLayout.setExpandRatio(schemaSelectorWidget, 1.0f);


        HorizontalLayout genHeader = new HorizontalLayout();
        this.addComponent(genHeader);
        genHeader.addStyleName("header");
        Label genLabel = new Label("<h2>Test Generation</h2>", ContentMode.HTML);
        genHeader.addComponent(genLabel);
        genHeader.setComponentAlignment(genLabel, Alignment.MIDDLE_LEFT);
        genHeader.addComponent(generateTestsButton);
        genHeader.setComponentAlignment(generateTestsButton, Alignment.MIDDLE_CENTER);
        genHeader.addComponent(generateTestsProgress);
        genHeader.setComponentAlignment(generateTestsProgress, Alignment.MIDDLE_CENTER);
        genHeader.addComponent(generateTestsProgressLabel);
        genHeader.setComponentAlignment(generateTestsProgressLabel, Alignment.MIDDLE_CENTER);
        genHeader.addComponent(generateTestsCancelButton);
        genHeader.setComponentAlignment(generateTestsCancelButton, Alignment.MIDDLE_CENTER);
        this.startTestingButton.setEnabled(false);
        this.generateTestsCancelButton.setEnabled(false);
        generateTestsProgress.setEnabled(false);
        generateTestsProgress.setWidth("150px");
        this.addComponent(testGenerationComponent);


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
        this.startTestingButton.setEnabled(false);
        this.startTestingCancelButton.setEnabled(false);
        testingProgress.setEnabled(false);
        testingProgress.setWidth("150px");

        this.addComponent(this.testResultsComponent);
        //this.addComponent(this.resultsButton);
        this.resultsButton.setEnabled(false);
    }

    private void initInteractions() {
        examplesSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                Property property = valueChangeEvent.getProperty();
                RDFUnitConfiguration configuration = (RDFUnitConfiguration) property.getValue();
                if (configuration != null) {
                    setExampleConfiguration(configuration);
                    RDFUnitUISession.setRDFUnitConfiguration(configuration);
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

            @Override
            public void run() {

                createConfigurationFromUser();
                if (RDFUnitUISession.getRDFUnitConfiguration() != null) {
                    Source dataset = RDFUnitUISession.getRDFUnitConfiguration().getTestSource();

                    RDFUnitUISession.initRDFUnit();
                    RDFUnitUISession.getTestGeneratorExecutor().addTestExecutorMonitor(testGenerationComponent);

                    RDFUnitUISession.setTestSuite(
                            RDFUnitUISession.getTestGeneratorExecutor().generateTestSuite(
                                    RDFUnitUISession.getBaseDir() + "tests/",
                                    dataset,
                                    RDFUnitUISession.getRDFUnit().getAutoGenerators()));

                    if (RDFUnitUISession.getTestSuite().size() != 0) {
                        UI.getCurrent().access(new Runnable() {
                            @Override
                            public void run() {
                                startTestingButton.setEnabled(true);
                            }
                        });
                    }
                } else {
                    UI.getCurrent().access(new Runnable() {
                        @Override
                        public void run() {
                            generateTestsButton.setEnabled(true);
                        }
                    });
                }

            }
        }

        // Clicking the button creates and runs a work thread
        generateTestsButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

                generateTestsButton.setEnabled(false);
                final TestGenerationThread thread = new TestGenerationThread();
                thread.start();

                // Enable polling and set frequency to 0.5 seconds
                UI.getCurrent().setPollInterval(500);
            }
        });

        RDFUnitUISession.getTestGeneratorExecutor().addTestExecutorMonitor(new TestGeneratorExecutorMonitor() {
            private long count = 0;
            private long total = 0;
            private long tests = 0;

            @Override
            public void generationStarted(final Source source, final long numberOfSources) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        generateTestsCancelButton.setEnabled(true);
                        total = numberOfSources;
                        count = 0;
                        tests = 0;
                        generateTestsProgress.setEnabled(true);
                        generateTestsProgress.setValue(0.0f);
                        generateTestsProgressLabel.setValue("0/" + numberOfSources);
                    }
                });

            }

            @Override
            public void sourceGenerationStarted(Source source, TestGenerationType generationType) {
            }

            @Override
            public void sourceGenerationExecuted(final Source source, final TestGenerationType generationType, final long testsCreated) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        tests += testsCreated;
                        generateTestsProgress.setValue((float) count / total);
                        generateTestsProgressLabel.setValue(count + "/" + total);
                    }
                });
            }

            @Override
            public void generationFinished() {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        generateTestsProgress.setValue(1.0f);
                        generateTestsProgressLabel.setValue("Completed! Generated " + tests + " tests");
                        generateTestsCancelButton.setEnabled(false);
                        UI.getCurrent().setPollInterval(-1);
                    }
                });
            }
        });

        generateTestsCancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                RDFUnitUISession.getTestGeneratorExecutor().cancel();
            }
        });


        class TestExecutorThread extends Thread {
            @Override
            public void run() {

                //TODO make this cleaner
                if (RDFUnitUISession.getRDFUnitConfiguration() != null) {
                    Source dataset = RDFUnitUISession.getRDFUnitConfiguration().getTestSource();

                    RDFUnitUISession.getTestExecutor().addTestExecutorMonitor(testResultsComponent);
                    String resultsFile = RDFUnitUISession.getBaseDir() + "results/" + dataset.getPrefix() + ".results.ttl";
                    //TODO refactor this, do not use cache here
                    File f = new File(resultsFile);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                    RDFUnitUISession.getTestExecutor().execute(dataset, RDFUnitUISession.getTestSuite(), 3);
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

        RDFUnitUISession.getTestExecutor().addTestExecutorMonitor(new TestExecutorMonitor() {
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
                RDFUnitUISession.getTestExecutor().cancel();
            }
        });

    }

    private void clearConfigurations() {

        endpointField.setValue("");
        graphField.setValue("");
        schemaSelectorWidget.setSelections(new ArrayList<SchemaSource>());
        examplesSelect.select(null);

        testResultsComponent.clearTableRowsAndHide();
        testGenerationComponent.clearTableRowsAndHide();

        generateTestsButton.setEnabled(true);
        startTestingButton.setEnabled(false);
        resultsButton.setEnabled(false);

        generateTestsProgressLabel.setValue("0/0");
        testingProgressLabel.setValue("0/0");

    }

    private void createConfigurationFromUser() {

        String datasetURI = endpointField.getValue().replace("/sparql", "");

        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetURI, RDFUnitUISession.getBaseDir());
        configuration.setEndpointConfiguration(endpointField.getValue(), Arrays.asList(graphField.getValue()));
        configuration.setSchemata(schemaSelectorWidget.getSelections());

        RDFUnitUISession.setRDFUnitConfiguration(configuration);
    }

    private void setExampleConfiguration(RDFUnitConfiguration configuration) {
        if (!(endpointField.getValue().isEmpty() || graphField.getValue().isEmpty() || schemaSelectorWidget.getSelections().isEmpty())) {
            //TODO confirm dialog for clear
            clearConfigurations();
        }

        Source dataset = configuration.getTestSource();
        if (dataset instanceof DatasetSource) {
            endpointField.setValue(((DatasetSource) dataset).getSparqlEndpoint());
            java.util.Collection<String> graphs = ((DatasetSource) dataset).getSparqlGraphs();
            graphField.setValue(RDFUnitUtils.getFirstItemInCollection(graphs));
        } else {
            endpointField.setValue("");
            graphField.setValue("");
        }
        schemaSelectorWidget.setSelections(dataset.getReferencesSchemata());
    }
}