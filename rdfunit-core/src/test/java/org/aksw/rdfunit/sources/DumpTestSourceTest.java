package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/26/15 1:45 PM
 */
public class DumpTestSourceTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetExecutionFactoryEmpty() throws Exception {
        TestSource testSource = TestSourceFactory.createDumpTestSource("tmp", "non-resolvable-uri", RDFReaderFactory.createResourceReader("non-resolvable-uri"), new ArrayList<>());
        testSource.getExecutionFactory();
    }
}