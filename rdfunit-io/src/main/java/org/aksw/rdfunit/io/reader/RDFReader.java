package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

/**
 * Triple reader interface
 *
 * @author Dimitris Kontokostas
 * @since 11/14/13 8:33 AM
 * @version $Id: $Id
 */
public interface RDFReader {

    /**
     * Reads RDF and returns a {@code Model}
     *
     * @return a {@code Model} with the data read
     * @throws org.aksw.rdfunit.io.reader.RDFReaderException if any.
     */
    Model read() throws RDFReaderException ;

    /**
     * Reads RDF and writes the data in the Model provided by the user
     *
     * @param model the model we want to write our data into
     * @throws org.aksw.rdfunit.io.reader.RDFReaderException if any.
     */
    void read(Model model) throws RDFReaderException;

    /**
     * <p>readDataset.</p>
     *
     * @return a {@link org.apache.jena.query.Dataset} object.
     * @throws org.aksw.rdfunit.io.reader.RDFReaderException if any.
     */
    Dataset readDataset() throws RDFReaderException ;

    /**
     * <p>readDataset.</p>
     *
     * @param dataset a {@link org.apache.jena.query.Dataset} object.
     * @throws org.aksw.rdfunit.io.reader.RDFReaderException if any.
     */
    void readDataset(Dataset dataset) throws RDFReaderException ;
}
