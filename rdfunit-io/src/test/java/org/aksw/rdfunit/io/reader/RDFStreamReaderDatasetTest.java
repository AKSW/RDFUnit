package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)

public class RDFStreamReaderDatasetTest {

    private Dataset dataset;

    @Before
    public void setUp() throws Exception {
        Model defaultModel = ModelFactory.createDefaultModel();
        defaultModel.add(
                ResourceFactory.createResource("http://rdfunit.aksw.org"),
                OWL.sameAs,
                ResourceFactory.createResource("http://dbpedia.org/resource/Cool")
        );

        Model namedModel = ModelFactory.createDefaultModel();
        namedModel.add(
                ResourceFactory.createResource("http://rdfunit.aksw.org"),
                OWL.sameAs,
                ResourceFactory.createResource("http://dbpedia.org/resource/Super")
        );
        dataset = DatasetFactory.create(defaultModel);

        dataset.addNamedModel("http://rdfunit.aksw.org", namedModel);
    }


    @Parameterized.Parameters(name= "{index}: file: {0}")
    public static Collection<Object[]> resources() throws Exception {

        String baseResDir = "/org/aksw/rdfunit/io/";
        return Arrays.asList(new Object[][] {
                { baseResDir + "onetriple.nq"},
                { baseResDir + "onetriple.trig"},
        });
    }

    @Parameterized.Parameter
    public String resourceName;



    @Test
    public void testReader() throws Exception{
        Dataset readDatasaet = RDFReaderFactory.createResourceReader(resourceName).readDataset();
        Iterator<String> namedGraphs = dataset.listNames();
        while (namedGraphs.hasNext()) {
            String namedGraph = namedGraphs.next();
            assertTrue("Not equal graph: " + namedGraph, dataset.getNamedModel(namedGraph).isIsomorphicWith(readDatasaet.getNamedModel(namedGraph)));
        }
        assertTrue("default graph not the same", dataset.getDefaultModel().isIsomorphicWith(readDatasaet.getDefaultModel()));

    }




}