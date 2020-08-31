package org.aksw.rdfunit.statistics;

import java.util.Map;
import org.junit.Test;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 4:02 PM
 */
public class DatasetStatisticsAllIrisTest extends DatasetStatisticsTest {

  private static final int EXPECTED_ITEMS = 19;

  @Override
  protected int getExteptedItems() {
    return EXPECTED_ITEMS;
  }

  @Override
  protected DatasetStatistics getStatisticsObject() {
    return new DatasetStatisticsAllIris();
  }

  @Test
  public void testGetStats() {
    for (Map.Entry<String, Long> entry : executeBasicTest().entrySet()) {
      assertEquals(Long.valueOf(0), entry.getValue());
    }
  }
}