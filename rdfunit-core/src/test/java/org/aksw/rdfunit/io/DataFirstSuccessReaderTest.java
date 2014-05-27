package org.aksw.rdfunit.io;

import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DataFirstSuccessReaderTest {

    @Test
    public void testRead() throws Exception {
        ArrayList<DataReader> dataReaders = new ArrayList<>();

        DataFirstSuccessReader reader = new DataFirstSuccessReader(dataReaders);
        try {
            reader.read();
            fail("Should have raised a TripleReaderException");
        } catch (TripleReaderException e) { }

        dataReaders.add(new RDFStreamReader(""));

        reader = new DataFirstSuccessReader(dataReaders);
        try {
            reader.read();
            fail("Should have raised a TripleReaderException");
        } catch (TripleReaderException e) { }

        dataReaders.add(new RDFStreamReader(DataFirstSuccessReaderTest.class.getResourceAsStream("/data/empty.ttl")));

        reader = new DataFirstSuccessReader(dataReaders);
        try {
            reader.read();
        } catch (TripleReaderException e) {
            fail("Should have NOT raised a TripleReaderException");
        }
    }
}