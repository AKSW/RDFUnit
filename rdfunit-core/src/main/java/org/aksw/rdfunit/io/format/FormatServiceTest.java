package org.aksw.rdfunit.io.format;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormatServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetInputFormat() throws Exception {
        // Turtle
        Assert.assertEquals(FormatService.getInputFormat("turtle"), SerialiazationFormatFactory.createTurtle());
        Assert.assertEquals(FormatService.getInputFormat("ttl"), SerialiazationFormatFactory.createTurtle());

        //N-Triples
        Assert.assertEquals(FormatService.getInputFormat("ntriples"), SerialiazationFormatFactory.createNTriples());
        Assert.assertEquals(FormatService.getInputFormat("N-Triple"), SerialiazationFormatFactory.createNTriples());

        //N3
        Assert.assertEquals(FormatService.getInputFormat("n3"), SerialiazationFormatFactory.createN3());

        // RDF/XML
        Assert.assertEquals(FormatService.getInputFormat("RDF/XML"), SerialiazationFormatFactory.createRDFXMLIn());
        Assert.assertEquals(FormatService.getInputFormat("rdfxml"), SerialiazationFormatFactory.createRDFXMLIn());
        Assert.assertEquals(FormatService.getInputFormat("RDF/XML-ABBREV"), SerialiazationFormatFactory.createRDFXMLIn());

    }

    @Test
    public void testGetOutputFormat() throws Exception {
        // Turtle
        Assert.assertEquals(FormatService.getOutputFormat("turtle"), SerialiazationFormatFactory.createTurtle());
        Assert.assertEquals(FormatService.getOutputFormat("ttl"), SerialiazationFormatFactory.createTurtle());

        //N-Triples
        Assert.assertEquals(FormatService.getOutputFormat("ntriples"), SerialiazationFormatFactory.createNTriples());
        Assert.assertEquals(FormatService.getOutputFormat("N-Triple"), SerialiazationFormatFactory.createNTriples());

        //N3
        Assert.assertEquals(FormatService.getOutputFormat("n3"), SerialiazationFormatFactory.createN3());

        // RDF/XML
        Assert.assertEquals(FormatService.getOutputFormat("RDF/XML"), SerialiazationFormatFactory.createRDFXMLOut());
        Assert.assertEquals(FormatService.getOutputFormat("rdfxml"), SerialiazationFormatFactory.createRDFXMLOut());

        // RDF/XML-ABBREV
        Assert.assertEquals(FormatService.getOutputFormat("RDF/XML-ABBREV"), SerialiazationFormatFactory.createRDFXMLAbbrevOut());
    }
}