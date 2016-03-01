package org.aksw.rdfunit.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestAppliesToTest {

    @Test
    public void testResolve() {
        for (TestAppliesTo appliesTo : TestAppliesTo.values()) {
            assertEquals(appliesTo, TestAppliesTo.resolve(appliesTo.getUri()));
        }
        assertNull(TestAppliesTo.resolve(""));
    }
}