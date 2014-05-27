package org.aksw.rdfunit.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RDFStreamReaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void checkInit() {
        // The following should not throw an exception
        new RDFStreamReader("");
        new RDFStreamReader(RDFStreamReaderTest.class.getResourceAsStream(""));
    }




}