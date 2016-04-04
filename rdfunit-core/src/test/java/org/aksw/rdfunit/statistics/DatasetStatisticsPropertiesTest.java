package org.aksw.rdfunit.statistics;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 6:45 PM
 */
public class DatasetStatisticsPropertiesTest extends DatasetStatisticsTest {


    private static final int EXPECTED_ITEMS = 6;

    @Override
    protected int getExteptedItems() {
        return EXPECTED_ITEMS;
    }

    @Override
    protected DatasetStatistics getStatisticsObject() {
        return new DatasetStatisticsProperties();
    }

    @Test
    public void testGetStats() {
        for (Map.Entry<String, Long> entry : executeBasicTest().entrySet()) {
            assertEquals(Long.valueOf(0), entry.getValue());
        }
    }
}