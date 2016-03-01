package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfMultipleReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
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
    public void setUp() throws RdfReaderException {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }

    @Parameterized.Parameters(name= "{index}: TestGenerator: {0}")
    public static Collection<Object[]> resources() throws RdfReaderException {

        Model model = new RdfMultipleReader(Arrays.asList(
                RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_OWL),
                RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_RS),
                RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_DSP)
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
    public void testGenerators() {
        TestGenerator tag = TestGeneratorReader.create().read(resource);
        assertTrue("TAG" + resource.getURI() + " is not valid", tag.isValid());
    }

}