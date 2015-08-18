package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.elements.interfaces.TestGenerator;
import org.aksw.rdfunit.io.reader.RDFMultipleReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * TAG Reader test
 * tries to read without error all defined generators
 *
 * @author Dimitris Kontokostas
 * @since 8/17/15 4:05 PM
 */
@RunWith(Parameterized.class)
public class TestGeneratorReaderTest {




    @Before
    public void setUp() throws Exception {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }

    @Parameterized.Parameters(name= "{index}: TestGenerator: {0}")
    public static Collection<Object[]> resources() throws Exception {

        String baseResDir = "/org/aksw/rdfunit/";
        String owlGen = baseResDir + "autoGeneratorsOWL.ttl";
        String dspGen = baseResDir + "autoGeneratorsDSP.ttl";
        String rsGen = baseResDir + "autoGeneratorsRS.ttl";

        Model model = new RDFMultipleReader(Arrays.asList(
                RDFReaderFactory.createResourceReader(owlGen),
                RDFReaderFactory.createResourceReader(dspGen),
                RDFReaderFactory.createResourceReader(rsGen)
        )).read();

        Collection<Object[]> parameters = new ArrayList<>();
        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList()) {
            parameters.add(new Object[] {resource});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;


    @Test
    public void testGenerators() throws Exception {
        TestGenerator tag = TestGeneratorReader.create().read(resource);
        assertTrue("TAG" + resource.getURI() + " is not valid", tag.isValid());
    }

}