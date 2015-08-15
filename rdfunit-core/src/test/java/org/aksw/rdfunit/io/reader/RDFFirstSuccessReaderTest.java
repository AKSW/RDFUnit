package org.aksw.rdfunit.io.reader;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RDFFirstSuccessReaderTest {

    @Test
    public void testRead() throws Exception {
        ArrayList<RDFReader> rdfReaders = new ArrayList<>();

        RDFFirstSuccessReader reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
            Assert.fail("Should have raised a TripleReaderException");
        } catch (RDFReaderException e) {
        }

        rdfReaders.add(new RDFStreamReader(""));

        reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
            Assert.fail("Should have raised a TripleReaderException");
        } catch (RDFReaderException e) {
        }

        rdfReaders.add(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/data/empty.ttl"));

        reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
        } catch (RDFReaderException e) {
            Assert.fail("Should have NOT raised a TripleReaderException");
        }
    }
}