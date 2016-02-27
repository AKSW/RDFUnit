package org.aksw.rdfunit.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PatternParameterConstraintsTest {

    @Test
    public void testResolve() throws Exception {
        for (PatternParameterConstraints level: PatternParameterConstraints.values()) {
            assertEquals(level, PatternParameterConstraints.resolve(level.getUri()));
        }
        assertEquals(PatternParameterConstraints.resolve(""), PatternParameterConstraints.None);
    }
}