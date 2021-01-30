package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Triple reader interface
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:33 AM
 */
public interface RdfReader {

  /**
   * Reads RDF and returns a {@code Model}
   *
   * @return a {@code Model} with the data read
   * @throws RdfReaderException if any.
   */
  default Model read() throws RdfReaderException {
    try {
      Model model = ModelFactory.createDefaultModel();
      read(model);
      return model;
    } catch (Exception e) {
      throw new RdfReaderException(e);
    }
  }

  /**
   * Reads RDF and writes the data in the Model provided by the user
   */
  void read(Model model) throws RdfReaderException;

  default Dataset readDataset() throws RdfReaderException {
    try {
      Dataset dataset = DatasetFactory.create();
      readDataset(dataset);
      return dataset;
    } catch (Exception e) {
      throw new RdfReaderException(e);
    }
  }

  void readDataset(Dataset dataset) throws RdfReaderException;
}