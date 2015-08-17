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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/17/15 4:05 PM
 */
public class TestGeneratorReaderTest {
    private Model model;
    private static final String baseResDir = "/org/aksw/rdfunit/";
    private static final String owlGen = baseResDir + "autoGeneratorsOWL.ttl";
    private static final int owlGeneratorsNum = 34;

    private static final String dspGen = baseResDir + "autoGeneratorsDSP.ttl";
    private static final int dspGeneratorsNum = 12;

    private static final String rsGen = baseResDir + "autoGeneratorsRS.ttl";
    private static final int rsGeneratorsNum = 12;

    @Before
    public void setUp() throws Exception {
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }

    @Test
    public void testOWLGenReader() throws Exception {
        testCustom(RDFReaderFactory.createResourceReader(owlGen).read(), owlGeneratorsNum);
    }

    @Test
    public void testDSPGenReader() throws Exception {
        testCustom(RDFReaderFactory.createResourceReader(dspGen).read(), dspGeneratorsNum);
    }

    @Test
    public void testRSGenReader() throws Exception {
        testCustom(RDFReaderFactory.createResourceReader(dspGen).read(), rsGeneratorsNum);
    }

    private void testCustom(Model model, int expectedSize) {
        Collection<TestGenerator> autoGenerators = new ArrayList<>();

        for (Resource r: model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList() ) {
            TestGenerator tag = TestGeneratorReader.createTestGeneratorReader().read(r);
            assertTrue("TAG" + r.getURI() + " is not valid", tag.isValid());
            autoGenerators.add(tag);
        }

        assertEquals(expectedSize, autoGenerators.size());
    }

}