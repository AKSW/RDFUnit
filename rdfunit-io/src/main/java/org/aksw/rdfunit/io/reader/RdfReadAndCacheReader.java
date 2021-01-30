package org.aksw.rdfunit.io.reader;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.writer.RdfWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * reads from a RDFReader and caches result
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 1:09 PM
 */
@Slf4j
public class RdfReadAndCacheReader implements RdfReader {

  private final RdfReader reader;
  private final RdfWriter writer;

  public RdfReadAndCacheReader(RdfReader reader, RdfWriter writer) {
    super();
    this.reader = reader;
    this.writer = writer;
  }


  @Override
  public void read(Model model) throws RdfReaderException {
    reader.read(model);
    //If read succeeds try to write
    try {
      writer.write(model);
    } catch (RdfWriterException e) {
      log.warn("Could not cache RdfReader", e);
    }
  }


  @Override
  public void readDataset(Dataset dataset) throws RdfReaderException {
    reader.readDataset(dataset);
    //If read succeeds try to write
    try {
      //TODO change this
      writer.write(dataset.getDefaultModel());
    } catch (RdfWriterException e) {
      log.warn("Could not cache RdfReader", e);
    }
  }


  @Override
  public String toString() {
    return "RDFReadAndCacheReader{" +
        "reader=" + reader +
        ", writer=" + writer +
        '}';
  }
}
