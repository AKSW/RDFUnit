package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
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
    public static Collection<Object[]> resources() throws Exception {
        Model model = RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/patterns.ttl").read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.Parameter).toList()) {
            parameters.add(new Object[] {resource});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;

    @Test
    public void testRead() throws Exception {
        PatternParameter patternParameter = PatternParameterReader.create().read(resource);
    }
}