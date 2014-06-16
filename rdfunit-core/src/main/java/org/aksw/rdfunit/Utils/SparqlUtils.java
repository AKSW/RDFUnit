package org.aksw.rdfunit.Utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.ArrayList;


/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/24/14 6:08 PM
 */
public final class SparqlUtils {

    private SparqlUtils() {
    }

    static public java.util.Collection<ResultAnnotation> getResultAnnotations(QueryExecutionFactory queryFactory, String uri) {
        java.util.Collection<ResultAnnotation> annotations = new ArrayList<>();
        String sparql = RDFUnitUtils.getAllPrefixes() +
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
            e.printStackTrace();
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return annotations;
    }

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

    public static boolean checkStatusForTimeout(QueryExceptionHTTP e) {
        int httpCode = e.getResponseCode();

        // 408,504,524 timeout codes from http://en.wikipedia.org/wiki/List_of_HTTP_status_codes
        return httpCode == 408 || httpCode == 504 || httpCode == 524;
    }

    public static Model getModelFromQueryFactory(QueryExecutionFactory qef) throws Exception {
        if (qef instanceof QueryExecutionFactoryModel) {
            return ((QueryExecutionFactoryModel) qef).getModel();
        } else {
            QueryExecution qe = null;
            try {
                qe = qef.createQueryExecution(" CONSTRUCT ?s ?p ?o WHERE { ?s ?p ?o } ");
                return qe.execConstruct();
            } catch (Exception e) {
                throw e;
            } finally {
                if (qe != null) {
                    qe.close();
                }
            }
        }
    }
}
