package org.aksw.rdfunit.enums;

import org.junit.Test;

public class PatternParameterConstraintsTest {

  @Test
  public void testResolve() {
    for (PatternParameterConstraints level : PatternParameterConstraints.values()) {
      assertEquals(level, PatternParameterConstraints.resolve(level.getUri()));
    }
    assertEquals(PatternParameterConstraints.resolve(""), PatternParameterConstraints.None);
  }
}