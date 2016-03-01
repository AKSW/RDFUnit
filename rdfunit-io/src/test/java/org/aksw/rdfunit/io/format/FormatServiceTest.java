package org.aksw.rdfunit.io.format;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class FormatServiceTest {


    @Test
    public void testGetInputFormat() {
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

        // RDFa
        assertEquals(FormatService.getInputFormat("RDFA"), SerialiazationFormatFactory.createRDFa());

    }

    @Test
    public void testGetOutputFormat() {
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

    @Test
    public void testGetFormatFromExtension() {
        Map<String, String> testVals = new HashMap<>();
        testVals.put("asdf.ttl", "TURTLE");
        testVals.put("asdf.nt", "N-TRIPLE");
        testVals.put("asdf.n3", "N3");
        testVals.put("asdf.jsonld", "JSON-LD");
        testVals.put("asdf.rj", "RDF/JSON");
        testVals.put("asdf.rdf", "RDF/XML");
        testVals.put("asdf.nq", "NQuads");
        testVals.put("asdf.trix", "TriX");
        testVals.put("asdf.trig", "TriG");
        testVals.put("asdf.html", "RDFA");

        for (Map.Entry<String, String> entry: testVals.entrySet()) {
            assertEquals("Should be equal", entry.getValue(), FormatService.getFormatFromExtension(entry.getKey()));
        }
    }
}