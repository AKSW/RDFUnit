package org.aksw.rdfunit.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class RLOGLevelTest {

  @Test
  public void testResolve() {

    for (RLOGLevel level : RLOGLevel.values()) {
      assertEquals(level, RLOGLevel.resolve(level.getUri()));
    }
    assertNull(RLOGLevel.resolve(""));
  }
}