package org.aksw.rdfunit.dqv;

import org.junit.Test;

public class MetricMapperTest {


  @Test
  public void testCreateDefault() {
    MetricMapper metricMapper = MetricMapper.createDefault();

    int DEFAULT_MAP_SIZE = 16;
    assertThat(metricMapper.getMetricMap().size())
        .isEqualTo(DEFAULT_MAP_SIZE);
  }
}