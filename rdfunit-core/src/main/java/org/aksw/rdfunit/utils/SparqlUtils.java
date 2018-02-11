package org.aksw.rdfunit.utils;

import lombok.extern.slf4j.Slf4j;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.model.impl.ResultAnnotationImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Dimitris Kontokostas
 * @since 1/24/14 6:08 PM
 */
@Slf4j
public final class SparqlUtils {

    private SparqlUtils() {
    }

    public static Collection<ResultAnnotation> getResultAnnotations(QueryExecutionFactory queryFactory, String uri) {
        Collection<ResultAnnotation> annotations = new ArrayList<>();
        String sparql = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
                " SELECT ?annotationProperty ?annotationValue WHERE {" +
                " <" + uri + "> rut:resultAnnotation ?annotation . " +
                " ?annotation a rut:ResultAnnotation ; " +
                "   rut:annotationProperty ?annotationProperty ; " +
                "   rut:annotationValue ?annotationValue . } ";



        try (QueryExecution qe = queryFactory.createQueryExecution(sparql)) {

            qe.execSelect().forEachRemaining(qs -> {
                String annotationProperty = qs.get("annotationProperty").toString();
                RDFNode annotationValue = qs.get("annotationValue");
                annotations.add(new ResultAnnotationImpl.Builder(uri, annotationProperty).setValueRDFUnit(annotationValue).build());
            });

        } catch (Exception e) {
            log.error("Error in sparql query",e);
            log.debug(sparql);
        }

        return annotations;
    }

    public static boolean checkAskQuery(QueryExecutionFactory qef, String askQuery) {
        QueryExecution qe = null;

        try {
            qe = qef.createQueryExecution(askQuery);
            return qe.execAsk();
        } catch (Exception e) {
            log.debug("Exception when running query {}", askQuery, e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
        return false;
    }

    public static boolean checkStatusForTimeout(QueryExceptionHTTP e) {
        int httpCode = e.getResponseCode();

        // 408,504,524 timeout codes from http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        return httpCode == 408 || httpCode == 504 || httpCode == 524;
    }
}
