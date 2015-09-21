package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

public class RdfUnitJunitRunnerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RdfUnitJunitRunner underTest;

    @Before
    public void setup() throws InitializationError {
        underTest = new RdfUnitJunitRunner(RdfUnitTest.class);
    }

    @Test
    public void descriptionIsNotATest() throws InitializationError {
        assertThat(underTest.getDescription().isTest()).isFalse();
    }

    @Test
    public void generatesTests() {
        assertThat(underTest.testCount()).isGreaterThan(1);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI, format = "RDF")
    public static class RdfUnitTest {
        @InputModel
        public Model inputModel() { return null; }
    }

    @Test(expected = InitializationError.class)
    public void throwsInitializationErrorIfOntologyIsMissing() throws InitializationError {
        new RdfUnitJunitRunner(RDFUnitMissingOntologyAnnotation.class);
    }

    @RunWith(RdfUnitJunitRunner.class)
    public static class RDFUnitMissingOntologyAnnotation {
    }
}
