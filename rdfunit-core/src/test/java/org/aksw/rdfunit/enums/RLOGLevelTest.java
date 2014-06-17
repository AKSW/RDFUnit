package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RLOGLevelTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testResolve() throws Exception {

        for (RLOGLevel level: RLOGLevel.values()) {
            assertEquals(level, RLOGLevel.resolve(level.getUri()));
        }
        assertNull(RLOGLevel.resolve(""));
    }
}