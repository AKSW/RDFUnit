package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael Leuthold
 * @version $Id: $Id
 */
public class RdfUnitJunitRunnerTest {

    public static final RDFReader CONTROLLED_VOCABULARY = newEmptyModelRdfReader();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RdfUnitJunitRunner underTest;

    private static RDFReader newEmptyModelRdfReader() {
        return new RDFModelReader(ModelFactory.createDefaultModel());
    }

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

    @Test
    public void testInputReturningNullDoesNotThrowIntializationError() throws InitializationError {
        new RdfUnitJunitRunner(RdfUnitWithNullTestInputTest.class);
    }

    @Test(expected = InitializationError.class)
    public void testInputReturningWrongTypeThrowsIntializationError() throws InitializationError {
        new RdfUnitJunitRunner(RdfUnitWithWrongTestInputReturnTypeTest.class);
    }

    @Test(expected = InitializationError.class)
    public void throwsInitializationErrorIfOntologyIsMissing() throws InitializationError {
        new RdfUnitJunitRunner(RDFUnitMissingOntologyAnnotation.class);
    }

    @Test
    public void returnsVocabulary() throws InitializationError {
        final RdfUnitJunitRunner rdfUnitJunitRunner = new RdfUnitJunitRunner(ControlledVocabularyTest.class);
        assertThat(rdfUnitJunitRunner.getAdditionalDataModel()).isSameAs(CONTROLLED_VOCABULARY);
    }

    @Test(expected = InitializationError.class)
    public void returnsTwoVocabulariesNotAllowed() throws InitializationError {
        new RdfUnitJunitRunner(TwoControlledVocabulariesNotAllowedTest.class);
    }

    @Test(expected = InitializationError.class)
    public void vocabularyWithWrongReturnTypeNotAllowed() throws InitializationError {
        new RdfUnitJunitRunner(VocabularyWithWrongReturnTypeNotAllowedTest.class);
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class RdfUnitTest {
        @TestInput
        public RDFReader testInput() {
            return newEmptyModelRdfReader();
        }
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class RdfUnitWithWrongTestInputReturnTypeTest {
        @TestInput
        public Model testInput() {
            return ModelFactory.createDefaultModel();
        }
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class RdfUnitWithNullTestInputTest {
        @TestInput
        public RDFReader testInput() {
            return null;
        }
    }

    @RunWith(RdfUnitJunitRunner.class)
    public static class RDFUnitMissingOntologyAnnotation {
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class ControlledVocabularyTest {

        @TestInput
        public RDFReader testInput() {
            return newEmptyModelRdfReader();
        }

        @AdditionalData
        public RDFReader vocabulary() {
            return CONTROLLED_VOCABULARY;
        }
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class TwoControlledVocabulariesNotAllowedTest {
        @TestInput
        public RDFReader testInput() {
            return newEmptyModelRdfReader();
        }

        @AdditionalData
        public RDFReader vocabulary1() {
            return CONTROLLED_VOCABULARY;
        }

        @AdditionalData
        public RDFReader vocabulary2() {
            return CONTROLLED_VOCABULARY;
        }
    }

    @RunWith(RdfUnitJunitRunner.class)
    @Schema(uri = Constants.FOAF_ONTOLOGY_URI)
    public static class VocabularyWithWrongReturnTypeNotAllowedTest {

        @TestInput
        public RDFReader testInput() {
            return newEmptyModelRdfReader();
        }

        @AdditionalData
        public void vocabulary1() {
        }
    }

}
