package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/18/15 12:51 AM
 */
@RunWith(Parameterized.class)
public class PatternReaderTest {


    @Parameters(name= "{index}: Pattern: {0}")
    public static Collection<Object[]> resources() throws Exception {
        Model model = RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/patterns.ttl").read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.Pattern).toList()) {
            parameters.add(new Object[] {resource});
        }
        return parameters;
    }

    @Parameter
    public Resource resource;


    @Test
    public void testRead() throws Exception {
        Pattern pattern = PatternReader.create().read(resource);
        assertTrue(pattern.isValid());
    }
}