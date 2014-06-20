package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.exceptions.TripleWriterException;

/**
 * @author Dimitris Kontokostas
 *         Triple writer superclass (could be an interface)
 * @since 11/14/13 12:59 PM
 */
public abstract class DataWriter {
    public void write(Model model) throws TripleWriterException {
        write(new QueryExecutionFactoryModel(model));
    }

    public abstract void write(QueryExecutionFactory qef) throws TripleWriterException;
}
