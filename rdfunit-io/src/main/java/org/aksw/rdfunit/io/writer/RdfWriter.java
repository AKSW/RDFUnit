package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;

/**
 * Triple writer interface
 *
 * @author Dimitris Kontokostas
 * @since 11 /14/13 12:59 PM
 * @version $Id: $Id
 */
public interface RdfWriter {

    /**
     * Writes a model into a destination. This function delegates to {@code write(QueryExecutionFactory qef)}
     *
     * @param model the model
     * @throws RdfWriterException the triple writer exception
     */
    default void write(Model model) throws RdfWriterException {
        write(new QueryExecutionFactoryModel(model));
    }


    /**
     * abstract class that writes a {@code QueryExecutionFactory} to a destination
     *
     * @param qef a QueryExecutionFactory
     * @throws RdfWriterException the triple writer exception
     */
    void write(QueryExecutionFactory qef) throws RdfWriterException;
}
