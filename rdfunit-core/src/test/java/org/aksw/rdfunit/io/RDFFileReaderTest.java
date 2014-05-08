package org.aksw.rdfunit.io;

import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class RDFFileReaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void checkInit() {
        // The following should not throw an exception
        new RDFFileReader("");
        new RDFFileReader(RDFFileReaderTest.class.getResourceAsStream(""));
    }




}