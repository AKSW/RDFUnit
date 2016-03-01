package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RSIntegrationTest {




    @Parameterized.Parameters(name = "{index}: {0} ({1})")
    public static Collection<Object[]> resources() {
        return Arrays.asList(new Object[][]{

                {"rs/valueType_Correct.ttl", 0},
                {"rs/valueType_Wrong.ttl", 11},
                {"rs/occurs_Correct.ttl", 0},
                {"rs/occurs_Wrong.ttl", 4},
                {"rs/range_Correct.ttl", 0},
                {"rs/range_Wrong.ttl", 4},
                {"rs/allowedValue_Correct.ttl", 0},
                {"rs/allowedValue_Wrong.ttl", 9},
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
                IntegrationTestHelper.getRsTestSuite(),
                IntegrationTestHelper.getRsSchemaSource());
    }

}