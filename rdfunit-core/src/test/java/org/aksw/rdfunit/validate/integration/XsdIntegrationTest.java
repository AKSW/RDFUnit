package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class XsdIntegrationTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1})")
    public static Collection<Object[]> resources() {
        return Arrays.asList(new Object[][]{
                {"xsd/XSD_unsignedlong_correct.ttl", 0},
                {"xsd/XSD_unsignedlong_wrong.ttl", 4},
                {"xsd/XSD_duration_correct.ttl", 0},
                {"xsd/XSD_duration_wrong.ttl", 10},
                {"xsd/XSD_byte_correct.ttl", 0},
                {"xsd/XSD_byte_wrong.ttl", 3},
                {"xsd/XSD_time_correct.ttl", 0},
                {"xsd/XSD_time_wrong.ttl", 6},
                {"xsd/XSD_float_correct.ttl", 0},
                {"xsd/XSD_float_wrong.ttl", 4},
                {"xsd/XSD_integer_correct.ttl", 0},
                {"xsd/XSD_integer_wrong.ttl", 5},
                {"xsd/XSD_boolean_correct.ttl", 0},
                {"xsd/XSD_boolean_wrong.ttl", 3},
                {"xsd/XSD_decimal_correct.ttl", 0},
                {"xsd/XSD_decimal_wrong.ttl", 2},
                {"xsd/XSD_date_correct.ttl", 0},
                {"xsd/XSD_date_wrong.ttl", 6},
        });

    }

    @Parameterized.Parameter
    public String testSource;

    @Parameterized.Parameter(value = 1)
    public int expectedErrors;


    @Test
    public void test() throws RdfReaderException, RdfWriterException, IOException {

        IntegrationTestHelper.testMap(
                IntegrationTestHelper.getResourcePrefix() + testSource,
                expectedErrors,
                IntegrationTestHelper.getOwlTestSuite(),
                IntegrationTestHelper.getOwlSchemaSource());
    }

}