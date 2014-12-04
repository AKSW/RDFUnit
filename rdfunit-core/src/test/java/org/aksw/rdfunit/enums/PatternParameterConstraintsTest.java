package org.aksw.rdfunit.enums;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PatternParameterConstraintsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testResolve() throws Exception {
        for (PatternParameterConstraints level: PatternParameterConstraints.values()) {
            assertEquals(level, PatternParameterConstraints.resolve(level.getUri()));
        }
        assertEquals(PatternParameterConstraints.resolve(""), PatternParameterConstraints.None);
    }
}