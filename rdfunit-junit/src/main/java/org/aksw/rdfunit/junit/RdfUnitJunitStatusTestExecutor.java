package org.aksw.rdfunit.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.executors.RLOGTestExecutor;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationSelectFactory;
import org.aksw.rdfunit.tests.results.TestCaseResult;

final class RdfUnitJunitStatusTestExecutor extends RLOGTestExecutor {

    public RdfUnitJunitStatusTestExecutor() {
        super(new QueryGenerationSelectFactory());
    }

    Collection<TestCaseResult> runTest(RdfUnitJunitTestCase rdfUnitJunitTestCase)
            throws IllegalAccessException, InvocationTargetException {
        final TestSource modelSource = new TestSourceBuilder()
                .setPrefixUri("custom", "rdfunit")
                .setInMemReader(new RDFModelReader(rdfUnitJunitTestCase.getInputModel()))
                .setReferenceSchemata(rdfUnitJunitTestCase.getSchemaSource())
                .build();

        try {
            return this.executeSingleTest(modelSource, rdfUnitJunitTestCase
                    .getTestCase());
        } catch (TestCaseExecutionException e) {
            /// Should never happen (TM)
            throw new RuntimeException(e);
        }
    }
}
