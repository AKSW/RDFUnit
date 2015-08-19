package org.aksw.rdfunit.io.reader;

import org.junit.Test;

public class RDFStreamReaderTest {

    @Test
    public void checkInit() {
        // The following should not throw an exception
        new RDFStreamReader("");
        new RDFStreamReader(RDFStreamReaderTest.class.getResourceAsStream(""), "TURTLE");
    }




}