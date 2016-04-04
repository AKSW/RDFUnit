package org.aksw.rdfunit.statistics;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 6:44 PM
 */
public class DatasetStatisticsClassesTest extends DatasetStatisticsTest {

    private static final int EXPECTED_ITEMS = 2;

    @Override
    protected int getExteptedItems() {
        return EXPECTED_ITEMS;
    }

    @Override
    protected DatasetStatistics getStatisticsObject() {
        return new DatasetStatisticsClasses();
    }

    @Test
    public void testGetStats() {
        for (Map.Entry<String, Long> entry : executeBasicTest().entrySet()) {
            assertEquals(Long.valueOf(0), entry.getValue());
        }
    }
}