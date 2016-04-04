package org.aksw.rdfunit.statistics;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotEquals;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 6:58 PM
 */
public class DatasetStatisticsPropertiesCountTest extends DatasetStatisticsTest {


    private static final int EXPECTED_ITEMS = 6;

    @Override
    protected int getExteptedItems() {
        return EXPECTED_ITEMS;
    }

    @Override
    protected DatasetStatistics getStatisticsObject() {
        return new DatasetStatisticsPropertiesCount();
    }

    @Test
    public void testGetStats() {

        for (Map.Entry<String, Long> entry : executeBasicTest().entrySet()) {
            assertNotEquals(Long.valueOf(0), entry.getValue());
        }
    }

}