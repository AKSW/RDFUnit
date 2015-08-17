package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.elements.interfaces.TestGenerator;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * TAG Reader test
 * tries to read without error all defined generators
 *
 * @author Dimitris Kontokostas
 * @since 8/17/15 4:05 PM
 */
public class TestGeneratorReaderTest {

    private static final String baseResDir = "/org/aksw/rdfunit/";
    private static final String owlGen = baseResDir + "autoGeneratorsOWL.ttl";
    private static final String dspGen = baseResDir + "autoGeneratorsDSP.ttl";
    private static final String rsGen = baseResDir + "autoGeneratorsRS.ttl";

    @Before
    public void setUp() throws Exception {
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }

    @Test
    public void testOWLGenReader() throws Exception {
        testCustom(RDFReaderFactory.createResourceReader(owlGen).read());
    }

    @Test
    public void testDSPGenReader() throws Exception {
        testCustom(RDFReaderFactory.createResourceReader(dspGen).read());
    }

    @Test
    public void testRSGenReader() throws Exception {
        testCustom(RDFReaderFactory.createResourceReader(dspGen).read());
    }

    private void testCustom(Model model) {
        Collection<TestGenerator> autoGenerators = new ArrayList<>();

        for (Resource r: model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList() ) {
            TestGenerator tag = TestGeneratorReader.createTestGeneratorReader().read(r);
            assertTrue("TAG" + r.getURI() + " is not valid", tag.isValid());
            autoGenerators.add(tag);
        }
    }
}