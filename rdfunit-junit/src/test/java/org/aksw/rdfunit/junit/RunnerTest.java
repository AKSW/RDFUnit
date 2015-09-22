package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import static org.assertj.core.api.Assertions.assertThat;

public class RunnerTest {

    private static final RunNotifier notifier = new RunNotifier();
    private static final MockRunListener mockRunListener = new MockRunListener();

    @BeforeClass
    public static void addNotifierListener() {
        notifier.addListener(mockRunListener);
        Request.aClass(TestRunner.class).getRunner().run(notifier);
    }

    @Test
    public void runsFinished() {
        assertThat(mockRunListener.getTestsFinished()).isGreaterThan(0);
    }

    @Test
    public void runsFailed() {
        assertThat(mockRunListener.getTestsFailed()).isGreaterThan(0);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class TestRunner {

        @InputModel
        public Model getInputData() {
            return ModelFactory
                    .createDefaultModel()
                    .read("https://raw.githubusercontent.com/RDFLib/rdflib/master/examples/foaf.rdf");
        }

    }

    private static class MockRunListener extends RunListener {
        private int testsFailed;
        private int testsFinished;

        @Override
        public void testFinished(Description description) throws Exception {
            testsFinished++;
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            testsFailed++;
        }

        public int getTestsFailed() {
            return testsFailed;
        }

        public int getTestsFinished() {
            return testsFinished;
        }
    }
}
