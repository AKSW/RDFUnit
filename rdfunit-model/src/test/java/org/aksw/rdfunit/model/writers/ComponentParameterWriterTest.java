package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.ComponentParameter;
import org.aksw.rdfunit.model.readers.ComponentParameterReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:42 AM
 */
@RunWith(Parameterized.class)
public class ComponentParameterWriterTest {

    @Parameterized.Parameters(name= "{index}: Pattern: {0}")
    public static Collection<Object[]> resources() throws RdfReaderException {
        Model model = RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/shacl/shacl.ttl").read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (RDFNode node: model.listObjectsOfProperty(SHACL.parameter).toList()) {
            parameters.add(new Object[] {node});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;


    @Test
    public void testRead() {
        // read the ComponentParameter
        ComponentParameter componentParameter = ComponentParameterReader.create().read(resource);

        // write in new model
        Model m1 = ModelFactory.createDefaultModel();
        Resource r1 = ComponentParameterWriter.create(componentParameter).write(m1);
        // reread
        ComponentParameter componentParameter2 = ComponentParameterReader.create().read(r1);
        Model m2 = ModelFactory.createDefaultModel();
        ComponentParameterWriter.create(componentParameter2).write(m2);

        assertThat(m1.isIsomorphicWith(m2))
            .isTrue();
    }
}