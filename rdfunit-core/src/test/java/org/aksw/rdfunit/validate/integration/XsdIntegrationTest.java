package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class XsdIntegrationTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1})")
    public static Collection<Object[]> resources() {
        return Arrays.asList(new Object[][]{

                //{"xsd/isDoubleMatch.ttl", 2},
                {"xsd/isByteMatch.ttl", 2}
        });

    }

    @Parameterized.Parameter
    public String testSource;

    @Parameterized.Parameter(value = 1)
    public int expectedErrors;


    @Test
    public void test() throws RdfReaderException {
        IntegrationTestHelper.testMap(
                IntegrationTestHelper.getResourcePrefix() + testSource,
                expectedErrors,
                IntegrationTestHelper.getXsdTestSuite(),
                IntegrationTestHelper.getOwlSchemaSource());        //using same ontology as in owl tests
    }

}