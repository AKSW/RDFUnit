package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.enums.ShapeScopeType;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 5/2/2016 4:42 μμ
 */
public class ShapeReaderTest {

    private static final String shapeResource = "/org/aksw/rdfunit/shacl/sampleShape.ttl" ;

    @Test
    public void testRead() throws Exception {

        Model shapesModel = RDFReaderFactory.createResourceReader(shapeResource).read();

        List<Shape> shapes = shapesModel.listResourcesWithProperty(RDF.type, SHACL.Shape).toList()
                .stream()
                .map( r -> ShapeReader.create().read(r))
                .collect(Collectors.toList());

        assertThat(shapes)
                .hasSize(1);

        Shape sh = shapes.get(0);

        assertThat(sh.getScopes())
                .hasSize(ShapeScopeType.values().length);


        List<ShapeScopeType> scopeTypes = sh.getScopes().stream()
                .map(ShapeScope::getScopeType)
                .distinct()
                .collect(Collectors.toList());

        // distinct scopes
        assertThat(scopeTypes)
                .hasSize(ShapeScopeType.values().length)
                ;
    }
}