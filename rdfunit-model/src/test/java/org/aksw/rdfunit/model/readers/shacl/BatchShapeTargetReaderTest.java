package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dimitris Kontokostas
 * @since 24/2/2016 4:29 μμ
 */
public class BatchShapeTargetReaderTest {

    private static final String shapeResource = "/org/aksw/rdfunit/shacl/sampleShapeTarget.ttl" ;

    @Before
    public void setUp() throws RdfReaderException {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }

    @Test
    public void testRead() throws RdfReaderException {

        Model shapesModel = RdfReaderFactory.createResourceReader(shapeResource).read();

        Resource r1 = shapesModel.getResource("http://example.org/MyShape");
        Set<ShapeTarget> targets1 = BatchShapeTargetReader.create().read(r1);
        assertThat(targets1)
                .hasSize(2);
    }

}