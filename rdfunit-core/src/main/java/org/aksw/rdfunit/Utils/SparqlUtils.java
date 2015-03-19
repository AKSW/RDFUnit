package org.aksw.rdfunit.Utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.results.ResultAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;


/**
 * <p>SparqlUtils class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/24/14 6:08 PM
 * @version $Id: $Id
 */
public final class SparqlUtils {
    private static final Logger log = LoggerFactory.getLogger(SparqlUtils.class);


    private SparqlUtils() {
    }

    /**
     * <p>getResultAnnotations.</p>
     *
     * @param queryFactory a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link java.util.Collection} object.
     */
    static public Collection<ResultAnnotation> getResultAnnotations(QueryExecutionFactory queryFactory, String uri) {
        Collection<ResultAnnotation> annotations = new ArrayList<>();
        String sparql = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT ?annotationProperty ?annotationValue WHERE {" +
                " <" + uri + "> rut:resultAnnotation ?annotation . " +
                " ?annotation a rut:ResultAnnotation ; " +
                "   rut:annotationProperty ?annotationProperty ; " +
                "   rut:annotationValue ?annotationValue . } ";

        QueryExecution qe = null;

        try {
            qe = queryFactory.createQueryExecution(sparql);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();
                String annotationProperty = qs.get("annotationProperty").toString();
                RDFNode annotationValue = qs.get("annotationValue");
                annotations.add(new ResultAnnotation(annotationProperty, annotationValue));
            }

        } catch (Exception e) {
            log.error("Error in sparql query",e);
            log.debug(sparql);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return annotations;
    }

    /**
     * <p>checkAskQuery.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param askQuery a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean checkAskQuery(QueryExecutionFactory qef, String askQuery) {
        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(askQuery);
            return qe.execAsk();
        } catch (Exception e) {

        } finally {
            if (qe != null) {
                qe.close();
            }
        }
        return false;
    }

    /**
     * <p>checkStatusForTimeout.</p>
     *
     * @param e a {@link com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP} object.
     * @return a boolean.
     */
    public static boolean checkStatusForTimeout(QueryExceptionHTTP e) {
        int httpCode = e.getResponseCode();

        // 408,504,524 timeout codes from http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        return httpCode == 408 || httpCode == 504 || httpCode == 524;
    }
}
