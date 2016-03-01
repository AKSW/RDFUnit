package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/18/15 12:33 AM
 */
@RunWith(Parameterized.class)
public class PatternParameterReaderTest {

    @Parameterized.Parameters(name= "{index}: Pattern Parameter: {0}")
    public static Collection<Object[]> resources() throws RdfReaderException {
        Model model = RdfReaderFactory.createResourceReader(Resources.PATTERNS).read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.Parameter).toList()) {
            parameters.add(new Object[] {resource});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;

    @Test
    public void testRead() {
        PatternParameterReader.create().read(resource);
    }
}