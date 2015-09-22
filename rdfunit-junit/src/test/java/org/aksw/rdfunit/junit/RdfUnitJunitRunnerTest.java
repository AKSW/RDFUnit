package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

public class RdfUnitJunitRunnerTest {

    public static final Model CONTROLLED_VOCABULARY = ModelFactory.createDefaultModel();

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
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class RdfUnitTest {
        @InputModel
        public Model inputModel() { return ModelFactory.createDefaultModel(); }
    }



    @Test(expected = InitializationError.class)
    public void inputModelNull() throws InitializationError {
        new RdfUnitJunitRunner(RdfUnitWithNullInputModelTest.class);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class RdfUnitWithNullInputModelTest {
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


    @Test
    public void returnsVocabulary() throws InitializationError {
        final RdfUnitJunitRunner rdfUnitJunitRunner = new RdfUnitJunitRunner(ControlledVocabularyTest.class);
        assertThat(rdfUnitJunitRunner.getControlledVocabularyModel()).isSameAs(CONTROLLED_VOCABULARY);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class ControlledVocabularyTest {

        @InputModel
        public Model inputModel() { return ModelFactory.createDefaultModel(); }

        @ControlledVocabulary
        public Model vocabulary() {
            return CONTROLLED_VOCABULARY;
        }
    }


    @Test(expected = InitializationError.class)
    public void returnsTwoVocabulariesNotAllowed() throws InitializationError {
        new RdfUnitJunitRunner(TwoControlledVocabulariesNotAllowedTest.class);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class TwoControlledVocabulariesNotAllowedTest {
        @InputModel
        public Model inputModel() { return ModelFactory.createDefaultModel(); }

        @ControlledVocabulary
        public Model vocabulary1() {
            return CONTROLLED_VOCABULARY;
        }

        @ControlledVocabulary
        public Model vocabulary2() {
            return CONTROLLED_VOCABULARY;
        }
    }


    @Test(expected = InitializationError.class)
    public void vocabularyWithWrongReturnTypeNotAllowed() throws InitializationError {
        new RdfUnitJunitRunner(VocabularyWithWrongReturnTypeNotAllowedTest.class);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Ontology(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class VocabularyWithWrongReturnTypeNotAllowedTest {

        @InputModel
        public Model inputModel() { return ModelFactory.createDefaultModel(); }

        @ControlledVocabulary
        public void vocabulary1() {}

    }

}
