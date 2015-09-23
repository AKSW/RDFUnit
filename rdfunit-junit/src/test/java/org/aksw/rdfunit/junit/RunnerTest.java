package org.aksw.rdfunit.junit;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void methodNamesMatchPattern() {
        for (Description d : mockRunListener.getDescriptions()) {
            assertThat(d.getMethodName()).matches("\\[getInputData\\] .*");
        }
    }

    @Test
    public void failuresMatchPattern() {
        for (Failure f : mockRunListener.getFailures()) {
            assertThat(f.getDescription().getMethodName()).matches("\\[getInputData\\] .*");
        }
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class TestRunner {

        @TestInput
        public Model getInputData() {
            return ModelFactory
                    .createDefaultModel()
                    .read("inputmodels/foaf.rdf");
        }

    }

    private static class MockRunListener extends RunListener {

        private final List<Description> descriptions = new ArrayList<>();
        private final List<Failure> failures = new ArrayList<>();

        @Override
        public void testFinished(Description description) throws Exception {
            descriptions.add(description);
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            failures.add(failure);
        }

        public List<Description> getDescriptions() {
            return descriptions;
        }

        public List<Failure> getFailures() {
            return failures;
        }

        public int getTestsFailed() {
            return failures.size();
        }

        public int getTestsFinished() {
            return descriptions.size();
        }
    }
}
