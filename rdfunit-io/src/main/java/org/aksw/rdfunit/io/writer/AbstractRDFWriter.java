package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.rdf.model.Model;

/**
 * Skeleton abstract class for RDFWriter
 *
 * @author Dimitris Kontokostas
 * @since 5/28/15 8:55 PM
 * @version $Id: $Id
 */
public abstract class AbstractRDFWriter implements RDFWriter {
    /** {@inheritDoc} */
    @Override
    public void write(Model model) throws RDFWriterException {
        write(new QueryExecutionFactoryModel(model));
    }

}
