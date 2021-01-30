package org.aksw.rdfunit.io.reader;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class RdfFirstSuccessReaderTest {

  @Test
  public void testEmptyRead() {
    ArrayList<RdfReader> rdfReaders = new ArrayList<>();

    RdfFirstSuccessReader reader = new RdfFirstSuccessReader(rdfReaders);
    try {
      reader.read();
      fail("Should have raised a TripleReaderException");
    } catch (RdfReaderException e) {
      // Good to be here
    }

    try {
      reader.readDataset();
      fail("Should have raised a TripleReaderException");
    } catch (RdfReaderException e) {
      // Good to be here
    }
  }

  @Test
  public void testExceptionRead() {
    ArrayList<RdfReader> rdfReaders = new ArrayList<>();
    rdfReaders.add(new RdfStreamReader(""));

    RdfFirstSuccessReader reader = new RdfFirstSuccessReader(rdfReaders);
    try {
      reader.read();
      fail("Should have raised a TripleReaderException");
    } catch (RdfReaderException e) {
      // Good to be here
    }

    try {
      reader.readDataset();
      fail("Should have raised a TripleReaderException");
    } catch (RdfReaderException e) {
      // Good to be here
    }

  }


  @Test
  public void testNotExceptionRead() {
    ArrayList<RdfReader> rdfReaders = new ArrayList<>();
    rdfReaders.add(new RdfStreamReader(""));

    rdfReaders.add(RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/io/empty.ttl"));

    RdfFirstSuccessReader reader = new RdfFirstSuccessReader(rdfReaders);
    try {
      reader.read();
    } catch (RdfReaderException e) {
      fail("Should have NOT raised a TripleReaderException");
    }
  }

  @Test
  public void testNotExceptionReadDataset() {
    ArrayList<RdfReader> rdfReaders = new ArrayList<>();
    rdfReaders.add(new RdfStreamReader(""));

    rdfReaders.add(RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/io/empty.ttl"));

    RdfFirstSuccessReader reader = new RdfFirstSuccessReader(rdfReaders);
    try {
      reader.readDataset();
    } catch (RdfReaderException e) {
      Assert.fail("Should have NOT raised a TripleReaderException");
    }
  }
}