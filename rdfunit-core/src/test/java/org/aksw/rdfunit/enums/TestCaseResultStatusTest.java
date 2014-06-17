package org.aksw.rdfunit.enums;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCaseResultStatusTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testResolve() throws Exception {
        for (TestCaseResultStatus resultStatus: TestCaseResultStatus.values()) {
            assertEquals(resultStatus, TestCaseResultStatus.resolve(resultStatus.getUri()));
        }

        assertEquals(TestCaseResultStatus.Error, TestCaseResultStatus.resolve(-2));
        assertEquals(TestCaseResultStatus.Timeout, TestCaseResultStatus.resolve(-1));
        assertEquals(TestCaseResultStatus.Success, TestCaseResultStatus.resolve(0));
        assertEquals(TestCaseResultStatus.Fail, TestCaseResultStatus.resolve(1));
        assertEquals(TestCaseResultStatus.Fail, TestCaseResultStatus.resolve(100));

        assertNull(TestCaseResultStatus.resolve(""));
    }
}