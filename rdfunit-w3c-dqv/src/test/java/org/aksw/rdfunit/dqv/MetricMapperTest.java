package org.aksw.rdfunit.dqv;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricMapperTest {

    private static int DEFAULT_MAP_SIZE = 16;


    @Test
    public void testCreateDefault() throws Exception {
        MetricMapper metricMapper = MetricMapper.createDefault();

        assertThat(metricMapper.getMetricMap().size())
                .isEqualTo(DEFAULT_MAP_SIZE);
    }
}