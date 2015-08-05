package org.aksw.rdfunit.io;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/17/14 4:48 PM
 */
public final class IOUtils {
    private IOUtils() {
    }

    /**
     * <p>getModelFromQueryFactory.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @return a {@link com.hp.hpl.jena.rdf.model.Model} object.
     */
    public static Model getModelFromQueryFactory(QueryExecutionFactory qef) {
        if (qef instanceof QueryExecutionFactoryModel) {
            return ((QueryExecutionFactoryModel) qef).getModel();
        } else {
            QueryExecution qe = null;
            try {
                qe = qef.createQueryExecution(" CONSTRUCT ?s ?p ?o WHERE { ?s ?p ?o } ");
                return qe.execConstruct();
            } finally {
                if (qe != null) {
                    qe.close();
                }
            }
        }
    }


    /**
     * <p>isURI.</p>
     *
     * @param uri a {@link java.lang.String} object.
     * @return a boolean.
     * @since 0.7.2
     */
    public static boolean isURI(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
