package org.aksw.rdfunit.validate.integration;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class OwlIntegrationTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1})")
    public static Collection<Object[]> resources() {
        return Arrays.asList(new Object[][]{

                {"owl/OWLDISJC_Correct.ttl", 0},
                {"owl/OWLDISJC_Wrong.ttl", 6},
                {"owl/RDFSRANGE_Correct.ttl", 0},
                {"owl/RDFSRANGE_Wrong.ttl", 1},
                {"owl/RDFSRANGE-MISS_Wrong.ttl", 1},
                {"owl/RDFSRANGED_Correct.ttl", 0},
                {"owl/RDFSRANGED_Wrong.ttl", 2},
                {"owl/INVFUNC_Correct.ttl", 0},
                {"owl/INVFUNC_Wrong.ttl", 2},
                {"owl/OWLCARDT_Correct.ttl", 0},
                {"owl/OWLCARDT_Wrong_Exact.ttl", 6},
                {"owl/OWLCARDT_Wrong_Min.ttl", 2},
                {"owl/OWLCARDT_Wrong_Max.ttl", 2},
                {"owl/OWLQCARDT_Correct.ttl", 0},
                {"owl/OWLQCARDT_Wrong_Exact.ttl", 6},
                {"owl/OWLQCARDT_Wrong_Min.ttl", 2},
                {"owl/OWLQCARDT_Wrong_Max.ttl", 2},
                {"owl/RDFLANGSTRING_Correct.ttl", 0},
                {"owl/RDFLANGSTRING_Wrong.ttl", 2},
                {"owl/RDFSRANG_LIT_Correct.ttl", 0},
                {"owl/RDFSRANG_LIT_Wrong.ttl", 3},


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
                IntegrationTestHelper.getOwlTestSuite(),
                IntegrationTestHelper.getOwlSchemaSource());
    }

}