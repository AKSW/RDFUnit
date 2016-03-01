package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DspIntegrationTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1})")
    public static Collection<Object[]> resources() {
        return Arrays.asList(new Object[][]{

                {"dsp/standalone_class_Correct.ttl", 0},
                {"dsp/standalone_class_Wrong.ttl", 1},
                {"dsp/property_cardinality_Correct.ttl", 0},
                {"dsp/property_cardinality_Wrong.ttl", 5},
                {"dsp/valueClass_Correct.ttl", 0},
                {"dsp/valueClass_Wrong.ttl", 1},
                {"dsp/valueClass-miss_Wrong.ttl", 1},
                {"dsp/languageOccurrence_Correct.ttl", 0},
                {"dsp/languageOccurrence_Wrong.ttl", 2},
                {"dsp/language_Correct.ttl", 0},
                {"dsp/language_Wrong.ttl", 3},
                {"dsp/isLiteral_Wrong.ttl", 1},
                {"dsp/literal_Correct.ttl", 0},
                {"dsp/literal_Wrong.ttl", 4},
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
                IntegrationTestHelper.getDspTestSuite(),
                IntegrationTestHelper.getDspSchemaSource());
    }

}