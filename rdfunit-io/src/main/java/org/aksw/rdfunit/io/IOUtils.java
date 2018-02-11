package org.aksw.rdfunit.io;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Dimitris Kontokostas
 * @since 12/17/14 4:48 PM

 */
public final class IOUtils {
    private IOUtils() {
    }

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


    public static boolean isURI(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isFile(String filename) {
        return new File(filename).exists();
    }
}
