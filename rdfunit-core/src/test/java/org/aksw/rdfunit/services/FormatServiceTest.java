package org.aksw.rdfunit.services;

import org.aksw.rdfunit.io.format.FormatTypeFactory;
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
        assertEquals(FormatService.getInputFormat("turtle"), FormatTypeFactory.createTurtle());
        assertEquals(FormatService.getInputFormat("ttl"), FormatTypeFactory.createTurtle());

        //N-Triples
        assertEquals(FormatService.getInputFormat("ntriples"), FormatTypeFactory.createNTriples());
        assertEquals(FormatService.getInputFormat("N-Triple"), FormatTypeFactory.createNTriples());

        //N3
        assertEquals(FormatService.getInputFormat("n3"), FormatTypeFactory.createN3());

        // RDF/XML
        assertEquals(FormatService.getInputFormat("RDF/XML"), FormatTypeFactory.createRDFXMLIn());
        assertEquals(FormatService.getInputFormat("rdfxml"), FormatTypeFactory.createRDFXMLIn());
        assertEquals(FormatService.getInputFormat("RDF/XML-ABBREV"), FormatTypeFactory.createRDFXMLIn());

    }

    @Test
    public void testGetOutputFormat() throws Exception {
        // Turtle
        assertEquals(FormatService.getOutputFormat("turtle"), FormatTypeFactory.createTurtle());
        assertEquals(FormatService.getOutputFormat("ttl"), FormatTypeFactory.createTurtle());

        //N-Triples
        assertEquals(FormatService.getOutputFormat("ntriples"), FormatTypeFactory.createNTriples());
        assertEquals(FormatService.getOutputFormat("N-Triple"), FormatTypeFactory.createNTriples());

        //N3
        assertEquals(FormatService.getOutputFormat("n3"), FormatTypeFactory.createN3());

        // RDF/XML
        assertEquals(FormatService.getOutputFormat("RDF/XML"), FormatTypeFactory.createRDFXMLOut());
        assertEquals(FormatService.getOutputFormat("rdfxml"), FormatTypeFactory.createRDFXMLOut());

        // RDF/XML-ABBREV
        assertEquals(FormatService.getOutputFormat("RDF/XML-ABBREV"), FormatTypeFactory.createRDFXMLAbbrevOut());
    }
}