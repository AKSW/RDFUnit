package org.aksw.rdfunit.io.writer;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

/**
 * Triple writer superclass (could be an interface if we remove {@code write(Model model)})
 *
 * @author Dimitris Kontokostas
 * @since 11 /14/13 12:59 PM
 * @version $Id: $Id
 */
public abstract class RDFWriter {

    /**
     * Writes a model into a destination. This function delegates to {@code write(QueryExecutionFactory qef)}
     *
     * @param model the model
     * @throws org.aksw.rdfunit.io.writer.RDFWriterException the triple writer exception
     */
    public void write(Model model) throws RDFWriterException {
        write(new QueryExecutionFactoryModel(model));
    }


    /**
     * abstract class that writes a {@code QueryExecutionFactory} to a destination
     *
     * @param qef a QueryExecutionFactory
     * @throws org.aksw.rdfunit.io.writer.RDFWriterException the triple writer exception
     */
    public abstract void write(QueryExecutionFactory qef) throws RDFWriterException;
}
