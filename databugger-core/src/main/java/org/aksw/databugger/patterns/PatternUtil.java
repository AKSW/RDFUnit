package org.aksw.databugger.patterns;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.Utils;
import org.aksw.jena_sparql_api.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 11:09 AM
 */
public class PatternUtil {
    public static List<Pattern> instantiatePatternsFromModel(QueryExecutionFactoryQuery queryFactory) {
        List<Pattern> patterns = new ArrayList<Pattern>();

        String sparqlSelectPatterns = Utils.getAllPrefixes() +
                        "SELECT ?pattern ?id ?desc ?sparql ?sparqlPrev ?variable WHERE { " +
                        " ?pattern a tddo:Pattern ; " +
                        "  dcterms:identifier ?id ; " +
                        "  dcterms:description ?desc ; " +
                        "  tddo:patternSPARQL ?sparql ; " +
                        "  tddo:patternPrevalence ?sparqlPrev ; " +
                        "  tddo:selectVariable ?variable . " +
                        "} ORDER BY ?pattern";
        String sparqlSelectParameters = Utils.getAllPrefixes() +
                        "SELECT ?parameter ?id  WHERE { " +
                        "?parameter a tddo:Parameter . " +
                        "?parameter dcterms:identifier ?id . " +
                        "%%PATTERN%%  tddo:parameter ?parameter . " +
                        "} ";


        Query q = QueryFactory.create(sparqlSelectPatterns);
        QueryExecution qe = queryFactory.createQueryExecution(q);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();

            String patternURI = qs.get("pattern").toString();
            String id = qs.get("id").toString();
            String desc = qs.get("desc").toString();
            String sparql = qs.get("sparql").toString();
            String sparqlPrev = qs.get("sparqlPrev").toString();
            String variable = qs.get("variable").toString();
            List<PatternParameter> parameters = new ArrayList<PatternParameter>();

            Query qn = QueryFactory.create(sparqlSelectParameters.replace("%%PATTERN%%", "<" + patternURI + ">"));
            QueryExecution qeNested = queryFactory.createQueryExecution(qn);
            ResultSet resultsNested = qeNested.execSelect();

            while (resultsNested.hasNext()) {

                QuerySolution parSol = resultsNested.next();

                String parameterID = parSol.get("id").toString();
                parameters.add(new PatternParameter(parameterID));
            }
            qeNested.close();

            patterns.add(new Pattern(id, desc, sparql, sparqlPrev, variable, parameters));

        }
        qe.close();

        return patterns;
    }
}
