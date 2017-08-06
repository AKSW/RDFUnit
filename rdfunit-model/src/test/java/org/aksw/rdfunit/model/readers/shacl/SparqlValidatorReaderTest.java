package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 8/6/17
 */
@RunWith(Parameterized.class)
public class SparqlValidatorReaderTest {

    @Parameterized.Parameters(name= "{index}: Validator: {0}")
    public static Collection<Object[]> resources() throws RdfReaderException {
        Model model = RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/shacl/sampleSparqlBasedConstraints.ttl").read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (RDFNode node: model.listObjectsOfProperty(SHACL.sparql).toList()) {
            parameters.add(new Object[] {node});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;

    @Test
    public void testRead() {
        SparqlValidatorReader.create().read(resource);
    }

}