package org.aksw.rdfunit.validate.wrappers;


import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

public class RDFUnitStaticValidatorTest {

    @Test
    public void testValidateModelShaclFullTestCaseResultMode() throws RDFReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RDFReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.shaclFullTestCaseResult);

    }

    @Test
    public void testValidateModelStatusTestCaseResultMode() throws RDFReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RDFReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.statusTestCaseResult);

    }


    @Test
    public void testValidateModelAggregatedTestCaseResultMode() throws RDFReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RDFReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.aggregatedTestCaseResult);

    }

    @Test
    public void testValidateModelShaclSimpleTestCaseResultMode() throws RDFReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RDFReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.shaclSimpleTestCaseResult);

    }
}
