package org.aksw.rdfunit.enums;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestAppliesToTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testResolve() throws Exception {
        for (TestAppliesTo appliesTo : TestAppliesTo.values()) {
            assertEquals(appliesTo, TestAppliesTo.resolve(appliesTo.getUri()));
        }
        assertNull(TestAppliesTo.resolve(""));
    }
}