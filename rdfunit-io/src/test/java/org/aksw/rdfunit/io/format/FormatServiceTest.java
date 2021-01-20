package org.aksw.rdfunit.io.format;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class FormatServiceTest {


  @Test
  public void testGetInputFormat() {
    // Turtle
    assertEquals(FormatService.getInputFormat("turtle"), SerializationFormatFactory.createTurtle());
    assertEquals(FormatService.getInputFormat("ttl"), SerializationFormatFactory.createTurtle());

    //N-Triples
    assertEquals(FormatService.getInputFormat("ntriples"),
        SerializationFormatFactory.createNTriples());
    assertEquals(FormatService.getInputFormat("N-Triple"),
        SerializationFormatFactory.createNTriples());

    //N3
    assertEquals(FormatService.getInputFormat("n3"), SerializationFormatFactory.createN3());

    // RDF/XML
    assertEquals(FormatService.getInputFormat("RDF/XML"),
        SerializationFormatFactory.createRDFXMLIn());
    assertEquals(FormatService.getInputFormat("rdfxml"),
        SerializationFormatFactory.createRDFXMLIn());
    assertEquals(FormatService.getInputFormat("RDF/XML-ABBREV"),
        SerializationFormatFactory.createRDFXMLIn());

    // RDFa
    assertEquals(FormatService.getInputFormat("RDFA"), SerializationFormatFactory.createRDFa());

  }

  @Test
  public void testGetOutputFormat() {
    // Turtle
    assertEquals(FormatService.getOutputFormat("turtle"),
        SerializationFormatFactory.createTurtle());
    assertEquals(FormatService.getOutputFormat("ttl"), SerializationFormatFactory.createTurtle());

    //N-Triples
    assertEquals(FormatService.getOutputFormat("ntriples"),
        SerializationFormatFactory.createNTriples());
    assertEquals(FormatService.getOutputFormat("N-Triple"),
        SerializationFormatFactory.createNTriples());

    //N3
    assertEquals(FormatService.getOutputFormat("n3"), SerializationFormatFactory.createN3());

    // RDF/XML
    assertEquals(FormatService.getOutputFormat("RDF/XML"),
        SerializationFormatFactory.createRDFXMLOut());
    assertEquals(FormatService.getOutputFormat("rdfxml"),
        SerializationFormatFactory.createRDFXMLOut());

    // RDF/XML-ABBREV
    assertEquals(FormatService.getOutputFormat("RDF/XML-ABBREV"),
        SerializationFormatFactory.createRDFXMLAbbrevOut());
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

    for (Map.Entry<String, String> entry : testVals.entrySet()) {
      assertEquals("Should be equal", entry.getValue(),
          FormatService.getFormatFromExtension(entry.getKey()));
    }
  }
}