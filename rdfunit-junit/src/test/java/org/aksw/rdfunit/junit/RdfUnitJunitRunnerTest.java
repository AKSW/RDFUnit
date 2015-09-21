package org.aksw.rdfunit.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

public class RdfUnitJunitRunnerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void descriptionIsNotATest() {
        assertThat(Request.aClass(RdfUnitTest.class).getRunner().getDescription().isTest()).isFalse();
    }

    @Test
    public void generatesTests() {
        assertThat(Request.aClass(RdfUnitTest.class).getRunner().testCount()).isGreaterThan(1);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI, format = "RDF")
    public static class RdfUnitTest {
    }


    @Test(expected = InitializationError.class)
    public void throwsInitializationErrorIfOntologyIsMissing() throws InitializationError {
        new RdfUnitJunitRunner(RDFUnitMissingOntologyAnnotation.class);
    }

    @RunWith(RdfUnitJunitRunner.class)
    public static class RDFUnitMissingOntologyAnnotation {
    }
}
