package org.aksw.rdfunit.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.StatusTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationAskFactory;

final class RdfUnitJunitStatusTestExecutor extends StatusTestExecutor {

    public RdfUnitJunitStatusTestExecutor() {
        super(new QueryGenerationAskFactory());
    }

    boolean runTest(RdfUnitJunitTestCase rdfUnitJunitTestCase)
            throws IllegalAccessException, InvocationTargetException {
        final TestSource modelSource = new TestSourceBuilder()
                .setPrefixUri("custom", "rdfunit")
                .setInMemReader(new RDFModelReader(rdfUnitJunitTestCase.getInputModel()))
                .setReferenceSchemata(rdfUnitJunitTestCase.getSchemaSource())
                .build();

        return this.execute(
                modelSource,
                new TestSuite(Collections.singleton(rdfUnitJunitTestCase.getTestCase()))
        );
    }
}
