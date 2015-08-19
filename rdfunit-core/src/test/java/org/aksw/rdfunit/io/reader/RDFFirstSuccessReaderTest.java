package org.aksw.rdfunit.io.reader;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class RDFFirstSuccessReaderTest {

    @Test
    public void testEmptyRead() throws Exception {
        ArrayList<RDFReader> rdfReaders = new ArrayList<>();

        RDFFirstSuccessReader reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
            Assert.fail("Should have raised a TripleReaderException");
        } catch (RDFReaderException e) {
        }

        try {
            reader.readDataset();
            Assert.fail("Should have raised a TripleReaderException");
        } catch (RDFReaderException e) {
        }
    }

    @Test
    public void testExceptionRead() throws Exception {
        ArrayList<RDFReader> rdfReaders = new ArrayList<>();
        rdfReaders.add(new RDFStreamReader(""));

        RDFFirstSuccessReader reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
            Assert.fail("Should have raised a TripleReaderException");
        } catch (RDFReaderException e) {
        }

        try {
            reader.readDataset();
            Assert.fail("Should have raised a TripleReaderException");
        } catch (RDFReaderException e) {
        }

    }


    @Test
    public void testNotExceptionRead() throws Exception {
        ArrayList<RDFReader> rdfReaders = new ArrayList<>();
        rdfReaders.add(new RDFStreamReader(""));

        rdfReaders.add(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/data/empty.ttl"));

        RDFFirstSuccessReader reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.read();
        } catch (RDFReaderException e) {
            Assert.fail("Should have NOT raised a TripleReaderException");
        }
    }

    @Test
    public void testNotExceptionReadDataset() throws Exception {
        ArrayList<RDFReader> rdfReaders = new ArrayList<>();
        rdfReaders.add(new RDFStreamReader(""));

        rdfReaders.add(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/data/empty.ttl"));

        RDFFirstSuccessReader reader = new RDFFirstSuccessReader(rdfReaders);
        try {
            reader.readDataset();
        } catch (RDFReaderException e) {
            Assert.fail("Should have NOT raised a TripleReaderException");
        }
    }
}