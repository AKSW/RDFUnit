package org.aksw.rdfunit.io.reader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RDFStreamReaderTest {

    @SuppressWarnings("PublicField")
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void checkInit() {
        // The following should not throw an exception
        new RDFStreamReader("");
        new RDFStreamReader(RDFStreamReaderTest.class.getResourceAsStream(""), "TURTLE");
    }

    @Test
    public void testGetFormatFromExtension() throws Exception {
        Map<String, String> testVals = new HashMap<>();
        testVals.put("asdf.ttl", "TURTLE");
        testVals.put("asdf.nt", "N-TRIPLE");
        testVals.put("asdf.n3", "N3");
        testVals.put("asdf.jsonld", "JSON-LD");
        testVals.put("asdf.rj", "RDF/JSON");
        testVals.put("asdf.rdf", "RDF/XML");

        for (Map.Entry<String, String> entry: testVals.entrySet()) {
            assertEquals("Should be equal", entry.getValue(), RDFStreamReader.getFormatFromExtension(entry.getKey()));
        }
    }


}