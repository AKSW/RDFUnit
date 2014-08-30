package org.aksw.rdfunit.webdemo.presenter;

import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.webdemo.RDFUnitDemoCommons;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.SchemaOption;
import org.aksw.rdfunit.webdemo.view.TestGenerationView;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:24 PM
 */
public class TestGenerationPresenter implements TestGenerationView.TestGenerationViewListener {

    private final TestGenerationView schemaSelectorView;

    private static final long fileLimit = 10*1024*1024;

    public TestGenerationPresenter(TestGenerationView schemaSelectorView) {
        this.schemaSelectorView = schemaSelectorView;

        schemaSelectorView.addListener(this);
    }

    @Override
    public boolean schemaIsSet(SchemaOption schemaOption, Collection<SchemaSource> schemaSources, String text, String format) {

        return false;

    }

    class TestGenerationThread extends Thread {

        @Override
        public void run() {

//                createConfigurationFromUser();
            if (RDFUnitDemoSession.getRDFUnitConfiguration() != null) {
                Source dataset = RDFUnitDemoSession.getRDFUnitConfiguration().getTestSource();



                RDFUnitDemoSession.setTestSuite(
                        RDFUnitDemoSession.getTestGeneratorExecutor().generateTestSuite(
                                RDFUnitDemoSession.getBaseDir() + "tests/",
                                dataset,
                                RDFUnitDemoCommons.getRDFUnit().getAutoGenerators()));

            }
//            else {
//                UI.getCurrent().access(new Runnable() {
//                    @Override
//                    public void run() {
//                        generateTestsButton.setEnabled(true);
//                    }
//                });
//            }

        }
    }
}
