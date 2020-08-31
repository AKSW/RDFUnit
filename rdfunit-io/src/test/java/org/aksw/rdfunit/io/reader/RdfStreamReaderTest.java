package org.aksw.rdfunit.io.reader;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)

public class RdfStreamReaderTest {

  @Parameterized.Parameter
  public String resourceName;
  private Model model;

  @Parameterized.Parameters(name = "{index}: file: {0}")
  public static Collection<Object[]> resources() {

    String baseResDir = "/org/aksw/rdfunit/io/";
    return Arrays.asList(new Object[][]{
        {baseResDir + "onetriple.nq"},
        {baseResDir + "onetriple.nt"},
        {baseResDir + "onetriple.rdf"},
        {baseResDir + "onetriple.rj"},
        {baseResDir + "onetriple.trig"},
        {baseResDir + "onetriple.ttl"},
        //{ baseResDir + "onetriple.html"},
    });
  }

  @Before
  public void setUp() {
    model = ReaderTestUtils.createOneTripleModel();
  }

  @Test
  public void testReader() throws Exception {
    Model readModel = RdfReaderFactory.createResourceReader(resourceName).read();
    assertTrue("Models not the same", model.isIsomorphicWith(readModel));

  }


}