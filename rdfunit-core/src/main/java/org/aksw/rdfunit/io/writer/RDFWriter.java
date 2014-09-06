package org.aksw.rdfunit.io.writer;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * Triple writer superclass (could be an interface if we remove {@code write(Model model)})
 *
 * @author Dimitris Kontokostas
 * @since 11 /14/13 12:59 PM
 */
public abstract class RDFWriter {

    /**
     * Writes a model into a destination. This function delegates to {@code write(QueryExecutionFactory qef)}
     *
     * @param model the model
     * @throws TripleWriterException the triple writer exception
     */
    public void write(Model model) throws TripleWriterException {
        write(new QueryExecutionFactoryModel(model));
    }


    /**
     * abstract class that writes a {@code QueryExecutionFactory} to a destination
     *
     * @param qef a QueryExecutionFactory
     * @throws TripleWriterException the triple writer exception
     */
    public abstract void write(QueryExecutionFactory qef) throws TripleWriterException;
}
