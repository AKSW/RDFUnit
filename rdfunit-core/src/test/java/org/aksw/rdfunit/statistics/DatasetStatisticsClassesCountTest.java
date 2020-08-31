package org.aksw.rdfunit.statistics;

import java.util.Map;
import org.junit.Test;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 6:52 PM
 */
public class DatasetStatisticsClassesCountTest extends DatasetStatisticsTest {

  private static final int EXPECTED_ITEMS = 2;

  @Override
  protected int getExteptedItems() {
    return EXPECTED_ITEMS;
  }

  @Override
  protected DatasetStatistics getStatisticsObject() {
    return new DatasetStatisticsClassesCount();
  }

  @Test
  public void testGetStats() {

    for (Map.Entry<String, Long> entry : executeBasicTest().entrySet()) {
      assertNotEquals(Long.valueOf(0), entry.getValue());
    }
  }
}