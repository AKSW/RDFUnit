package org.aksw.rdfunit.validate.wrappers;


import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@RunWith(Parameterized.class)
public class RDFUnitStaticValidatorTest {

    @Before
    public void setUp() {
        RDFUnitStaticValidator.initWrapper(
                new RDFUnitTestSuiteGenerator.Builder()
                        .addLocalResource("empty", "/org/aksw/rdfunit/validate/data/empty.ttl")
                        .build()
        );
    }

    @Parameterized.Parameters
    public static Collection<Object[]> resources() {
        Collection<Object[]> parameters = new ArrayList<>();
        for (TestCaseExecutionType t: TestCaseExecutionType.values()) {
            parameters.add(new Object[] {t});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public TestCaseExecutionType testCaseExecutionType;


    @Test
    public void testValidateModel() throws RdfReaderException {
        RDFUnitStaticValidator.validate(ModelFactory.createDefaultModel(), testCaseExecutionType);
    }

    @Test
    public void testValidateModelAsSource() throws RdfReaderException {
        RDFUnitStaticValidator.validate(testCaseExecutionType, ModelFactory.createDefaultModel(),RDFUnitStaticValidator.getTestSuite());

        // create new dataset for current entry
        final TestSource modelSource = new TestSourceBuilder()
                .setImMemSingle()
                .setPrefixUri("test", "http://example.com")
                .setInMemReader(new RdfModelReader(RdfReaderFactory.createResourceReader( "/org/aksw/rdfunit/validate/data/empty.ttl").read()))
                .setReferenceSchemata(Collections.emptyList())
                .build();
        RDFUnitStaticValidator.validate(testCaseExecutionType, modelSource, RDFUnitStaticValidator.getTestSuite());
    }

}
