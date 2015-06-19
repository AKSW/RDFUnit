package org.aksw.rdfunit.io.writer;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

/**
 * Skeleton abstract class for RDFWriter
 *
 * @author Dimitris Kontokostas
 * @since 5/28/15 8:55 PM
 */
public abstract class AbstractRDFWriter implements RDFWriter {
    @Override
    public void write(Model model) throws RDFWriterException {
        write(new QueryExecutionFactoryModel(model));
    }

}
