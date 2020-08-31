package org.aksw.rdfunit.model.readers.shacl;

import java.util.Set;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

/**
 * @author Dimitris Kontokostas
 * @since 24/2/2016 4:29 μμ
 */
public class BatchShapeTargetReaderTest {

  private static final String shapeResource = "/org/aksw/rdfunit/shacl/sampleShapeTarget.ttl";


  @Test
  public void testRead() throws RdfReaderException {

    Model shapesModel = RdfReaderFactory.createResourceReader(shapeResource).read();

    Resource r1 = shapesModel.getResource("http://example.org/MyShape");
    Set<ShapeTarget> targets1 = BatchShapeTargetReader.create().read(r1);
    assertThat(targets1)
        .hasSize(2);
  }

}