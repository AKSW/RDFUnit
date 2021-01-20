package org.aksw.rdfunit.io.reader;

import java.util.Collection;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:51 AM
 */
public class RdfMultipleReader implements RdfReader {

  private final Collection<RdfReader> readers;

  public RdfMultipleReader(Collection<RdfReader> readers) {
    super();
    this.readers = readers;
  }


  @Override
  public void read(Model model) throws RdfReaderException {

    for (RdfReader r : readers) {
      try {
        r.read(model);
      } catch (RdfReaderException e) {
        throw new RdfReaderException("Cannot read from reader", e);
      }
    }


  }


  @Override
  public void readDataset(Dataset dataset) throws RdfReaderException {

    for (RdfReader r : readers) {
      try {
        r.readDataset(dataset);
      } catch (RdfReaderException e) {
        throw new RdfReaderException("Cannot read from reader", e);
      }
    }


  }


  @Override
  public String toString() {
    return "RDFMultipleReader{" +
        "readers=" + readers +
        '}';
  }
}
