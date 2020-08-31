package org.aksw.rdfunit.enums;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestAppliesToTest {

  @Test
  public void testResolve() {
    for (TestAppliesTo appliesTo : TestAppliesTo.values()) {
      assertEquals(appliesTo, TestAppliesTo.resolve(appliesTo.getUri()));
    }
    assertNull(TestAppliesTo.resolve(""));
  }
}