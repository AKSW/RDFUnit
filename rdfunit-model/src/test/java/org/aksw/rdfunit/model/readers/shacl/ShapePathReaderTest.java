package org.aksw.rdfunit.model.readers.shacl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Dimitris Kontokostas
 * @since 7/20/17
 */

@RunWith(Parameterized.class)
public class ShapePathReaderTest {

  @Parameterized.Parameter(value = 0)
  public Resource shaclPath;
  @Parameterized.Parameter(value = 1)
  public String propertyPathString;

  @Parameterized.Parameters(name = "{index}: Path: {1}")
  public static Collection<Object[]> resources() throws RdfReaderException {
    Model model = RdfReaderFactory
        .createResourceReader("/org/aksw/rdfunit/model/readers/shacl/ShapePathReaderTest.ttl")
        .read();
    Collection<Object[]> parameters = new ArrayList<>();
    Property pathProperty = ResourceFactory.createProperty("http://ex.com/path");
    Property pathExprProperty = ResourceFactory.createProperty("http://ex.com/pathExp");
    for (Resource node : model.listSubjectsWithProperty(pathProperty).toList()) {
      Resource path = node.getPropertyResourceValue(pathProperty);
      String pathExpr = node.getProperty(pathExprProperty).getObject().asLiteral().getLexicalForm();
      parameters.add(new Object[]{path, pathExpr});
    }
    return parameters;
  }

  @Test
  public void testRead() {
    assertThat(ShapePathReader.create().read(shaclPath).asSparqlPropertyPath())
        .isEqualTo(propertyPathString);
  }
}