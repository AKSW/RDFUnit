package org.aksw.rdfunit.sources;

import java.util.ArrayList;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.junit.Test;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/26/15 1:45 PM
 */
public class DumpTestSourceTest {

  @Test(expected = IllegalArgumentException.class)
  public void testGetExecutionFactoryEmpty() {
    TestSource testSource = TestSourceFactory.createDumpTestSource("tmp", "non-resolvable-uri",
        RdfReaderFactory.createResourceReader("non-resolvable-uri"), new ArrayList<>());
    testSource.getExecutionFactory();
  }
}