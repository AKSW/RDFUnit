package org.aksw.rdfunit.validate.wrappers;


import org.aksw.rdfunit.enums.TestCaseExecutionType;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.Test;

public class RDFUnitStaticValidatorTest {

    @Test
    public void testValidateModelShaclFullTestCaseResultMode() throws RdfReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RdfReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.shaclFullTestCaseResult);

    }

    @Test
    public void testValidateModelStatusTestCaseResultMode() throws RdfReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RdfReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.statusTestCaseResult);

    }


    @Test
    public void testValidateModelAggregatedTestCaseResultMode() throws RdfReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RdfReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.aggregatedTestCaseResult);

    }

    @Test
    public void testValidateModelShaclSimpleTestCaseResultMode() throws RdfReaderException {
        //Init
        String turtle = "";

        //Act
        Model model = RdfReaderFactory.createReaderFromText(turtle, Lang.NTRIPLES.getName()).read();

        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResourceOrSchemaURI("nif", "org/uni-leipzig/persistence/nlp2rdf/nif-core/nif-core.ttl", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#")
                        .build()
        );
        RDFUnitStaticValidator.validate(model, TestCaseExecutionType.shaclSimpleTestCaseResult);

    }
}
