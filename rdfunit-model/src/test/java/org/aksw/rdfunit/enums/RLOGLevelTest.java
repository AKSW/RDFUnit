package org.aksw.rdfunit.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RLOGLevelTest {

    @Test
    public void testResolve() {

        for (RLOGLevel level : RLOGLevel.values()) {
            assertEquals(level, RLOGLevel.resolve(level.getUri()));
        }
        assertNull(RLOGLevel.resolve(""));
    }
}