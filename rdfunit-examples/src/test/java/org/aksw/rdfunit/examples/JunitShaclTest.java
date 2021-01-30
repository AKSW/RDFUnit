package org.aksw.rdfunit.examples;

import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.junit.RdfUnitJunitRunner;
import org.aksw.rdfunit.junit.Schema;
import org.aksw.rdfunit.junit.TestInput;
import org.junit.runner.RunWith;


@RunWith(RdfUnitJunitRunner.class)
@Schema(uri = "/org/aksw/rdfunit/examples/shacl.example.shapes.ttl")
public class JunitShaclTest {


  @TestInput
  public RdfReader getInputData() throws RdfReaderException {
    return new RdfModelReader(
        RdfReaderFactory.createResourceReader(
            "/org/aksw/rdfunit/examples/shacl.example.data.ttl").read());
  }

}
