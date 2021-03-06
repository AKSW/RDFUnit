package org.aksw.rdfunit.model.readers.shacl;

import java.util.ArrayList;
import java.util.Collection;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ComponentParameterReaderTest {

  @Parameterized.Parameter
  public Resource resource;

  @Parameterized.Parameters(name = "{index}: Parameter: {0}")
  public static Collection<Object[]> resources() throws RdfReaderException {
    Model model = RdfReaderFactory.createResourceReader(Resources.SHACL_CORE_CCs).read();
    Collection<Object[]> parameters = new ArrayList<>();
    for (RDFNode node : model.listObjectsOfProperty(SHACL.parameter).toList()) {
      parameters.add(new Object[]{node});
    }
    return parameters;
  }

  @Test
  public void testRead() {
    ComponentParameterReader.create().read(resource);

  }
}