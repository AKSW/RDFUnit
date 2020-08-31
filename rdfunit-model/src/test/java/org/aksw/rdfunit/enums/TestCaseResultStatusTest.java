package org.aksw.rdfunit.enums;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestCaseResultStatusTest {


  @Test
  public void testResolve() {
    for (TestCaseResultStatus resultStatus : TestCaseResultStatus.values()) {
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