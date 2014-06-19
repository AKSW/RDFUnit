package org.aksw.rdfunit.services;

import org.aksw.rdfunit.io.format.SerialiazationFormatFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
        assertEquals(FormatService.getInputFormat("turtle"), SerialiazationFormatFactory.createTurtle());
        assertEquals(FormatService.getInputFormat("ttl"), SerialiazationFormatFactory.createTurtle());

        //N-Triples
        assertEquals(FormatService.getInputFormat("ntriples"), SerialiazationFormatFactory.createNTriples());
        assertEquals(FormatService.getInputFormat("N-Triple"), SerialiazationFormatFactory.createNTriples());

        //N3
        assertEquals(FormatService.getInputFormat("n3"), SerialiazationFormatFactory.createN3());

        // RDF/XML
        assertEquals(FormatService.getInputFormat("RDF/XML"), SerialiazationFormatFactory.createRDFXMLIn());
        assertEquals(FormatService.getInputFormat("rdfxml"), SerialiazationFormatFactory.createRDFXMLIn());
        assertEquals(FormatService.getInputFormat("RDF/XML-ABBREV"), SerialiazationFormatFactory.createRDFXMLIn());

    }

    @Test
    public void testGetOutputFormat() throws Exception {
        // Turtle
        assertEquals(FormatService.getOutputFormat("turtle"), SerialiazationFormatFactory.createTurtle());
        assertEquals(FormatService.getOutputFormat("ttl"), SerialiazationFormatFactory.createTurtle());

        //N-Triples
        assertEquals(FormatService.getOutputFormat("ntriples"), SerialiazationFormatFactory.createNTriples());
        assertEquals(FormatService.getOutputFormat("N-Triple"), SerialiazationFormatFactory.createNTriples());

        //N3
        assertEquals(FormatService.getOutputFormat("n3"), SerialiazationFormatFactory.createN3());

        // RDF/XML
        assertEquals(FormatService.getOutputFormat("RDF/XML"), SerialiazationFormatFactory.createRDFXMLOut());
        assertEquals(FormatService.getOutputFormat("rdfxml"), SerialiazationFormatFactory.createRDFXMLOut());

        // RDF/XML-ABBREV
        assertEquals(FormatService.getOutputFormat("RDF/XML-ABBREV"), SerialiazationFormatFactory.createRDFXMLAbbrevOut());
    }
}