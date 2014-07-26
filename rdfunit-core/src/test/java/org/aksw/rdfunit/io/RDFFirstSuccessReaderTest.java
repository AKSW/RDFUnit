package org.aksw.rdfunit.io;

import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.fail;

public class RDFFirstSuccessReaderTest {

    @Test
    public void testRead() throws Exception {
        ArrayList<RDFReader> rdfReaders = new ArrayList<>();

        RDFFirstSuccessReader reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
            fail("Should have raised a TripleReaderException");
        } catch (TripleReaderException e) {
        }

        rdfReaders.add(new RDFStreamReader(""));

        reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
            fail("Should have raised a TripleReaderException");
        } catch (TripleReaderException e) {
        }

        rdfReaders.add(new RDFStreamReader(RDFFirstSuccessReaderTest.class.getResourceAsStream("/org/aksw/rdfunit/data/empty.ttl")));

        reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
        } catch (TripleReaderException e) {
            fail("Should have NOT raised a TripleReaderException");
        }
    }
}