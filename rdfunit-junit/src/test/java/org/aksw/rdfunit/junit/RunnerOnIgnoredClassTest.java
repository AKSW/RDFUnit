package org.aksw.rdfunit.junit;

import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.apache.jena.rdf.model.ModelFactory;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import static org.mockito.Mockito.*;

public class RunnerOnIgnoredClassTest {

    @Test
    public void verifyTestRunnerIsNotExecuted() throws Exception {
        RunNotifier notifier = new RunNotifier();
        RunListener listener = mock(RunListener.class);
        notifier.addListener(listener);

        Request.aClass(TestRunnerIgnoredOnClassLevel.class).getRunner().run(notifier);

        verify(listener, times(1)).testIgnored(any(Description.class));

        Assertions.assertThat(TestRunnerIgnoredOnClassLevel.ignoredTestInputMethodNotCalled).isTrue();
    }


    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.EXAMPLE_SCHEMA)
    @Ignore
    public static class TestRunnerIgnoredOnClassLevel {

        private static boolean ignoredTestInputMethodNotCalled = true;

        @TestInput
        public RdfReader ignoredTestInput() {
            ignoredTestInputMethodNotCalled = false;
            return new RdfModelReader(ModelFactory.createDefaultModel());
        }
    }
}
