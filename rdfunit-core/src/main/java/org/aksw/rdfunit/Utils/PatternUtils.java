package org.aksw.rdfunit.Utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.PatternParameterConstraints;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.patterns.PatternParameter;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/23/13 11:09 AM
 */
public final class PatternUtils {

    private PatternUtils() {
    }



    public static Collection<Pattern> instantiatePatternsFromModel(QueryExecutionFactory queryFactory) {

        final String sparqlSelectPatterns = PrefixNSService.getSparqlPrefixDecl() +
                "SELECT distinct ?sparqlPattern WHERE { " +
                " ?sparqlPattern a rut:Pattern . }";

        Collection<String> patternURIs = new ArrayList<>();


        QueryExecution qe = null;
        try {
            qe = queryFactory.createQueryExecution(sparqlSelectPatterns);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();

                patternURIs.add(qs.get("sparqlPattern").toString());
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        Collection<Pattern> patterns = new ArrayList<>();
        for (String patternURI : patternURIs) {
            patterns.add (getPattern(queryFactory, patternURI));
        }

        return patterns;
    }

    /**
     * Given a QueryExecutionFactory and a pattern URI, it instantiates a new Pattern
     *
     * @param queryFactory the query factory
     * @param patternURI the pattern uRI
     * @return the pattern object
     */
    public static Pattern getPattern(QueryExecutionFactory queryFactory, String patternURI) {
        final String sparqlSelectPattern = PrefixNSService.getSparqlPrefixDecl() +
                "SELECT distinct ?sparqlPattern ?id ?desc ?sparql ?sparqlPrev ?variable WHERE { " +
                "  <" + patternURI + "> a rut:Pattern ; " +
                "  dcterms:identifier ?id ; " +
                "  dcterms:description ?desc ; " +
                "  rut:sparqlWherePattern ?sparql ; " +
                "  rut:sparqlPrevalencePattern ?sparqlPrev ; " +
                "} ORDER BY ?sparqlPattern";

        Pattern pattern = null;
        QueryExecution qe = null;
        try {
            qe = queryFactory.createQueryExecution(sparqlSelectPattern);
            ResultSet results = qe.execSelect();

            if (results.hasNext()) {
                QuerySolution qs = results.next();

                String id = qs.get("id").toString();
                String desc = qs.get("desc").toString();
                String sparql = qs.get("sparql").toString();
                String sparqlPrev = qs.get("sparqlPrev").toString();

                // Get pattern parameters
                Collection<PatternParameter> parameters = getPatternParameters(queryFactory, patternURI);

                // Get annotations from TAG URI
                Collection<ResultAnnotation> annotations = SparqlUtils.getResultAnnotations(queryFactory, patternURI);

                pattern = new Pattern(id, desc, sparql, sparqlPrev, parameters, annotations);

                // if not valid OR if multiple results returns something is wrong
                if (!pattern.isValid() || results.hasNext()) {
                    throw new IllegalArgumentException("Pattern not valid: " + pattern.getId());
                }
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return pattern;
    }

    private static Collection<PatternParameter> getPatternParameters(QueryExecutionFactory queryFactory, String patternURI) {

        final String sparqlSelectParameters = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT distinct ?parameterURI ?id ?constraint ?constraintPattern WHERE { " +
                " %%PATTERN%%  rut:parameter ?parameterURI . " +
                " ?parameterURI a rut:Parameter . " +
                " ?parameterURI dcterms:identifier ?id . " +
                " OPTIONAL {?parameterURI rut:parameterConstraint ?constraint .}" +
                " OPTIONAL {?parameterURI rut:constraintPattern ?constraintPattern .}" +
                " } ";

        Collection<PatternParameter> parameters = new ArrayList<>();

        QueryExecution qe = null;
        try {
            qe = queryFactory.createQueryExecution(sparqlSelectParameters.replace("%%PATTERN%%", "<" + patternURI + ">"));
            ResultSet rs = qe.execSelect();

            while (rs.hasNext()) {

                QuerySolution parSol = rs.next();

                String parameterURI = parSol.get("parameterURI").toString();
                String parameterID = parSol.get("id").toString();
                String constrainStr = "";
                if (parSol.contains("constrain")) {
                    constrainStr = parSol.get("constrain").toString();
                }
                String constrainPat = "";
                if (parSol.contains("constraintPattern")) {
                    constrainPat = parSol.get("constraintPattern").toString();
                }

                PatternParameterConstraints constrain = PatternParameterConstraints.resolve(constrainStr);
                parameters.add(new PatternParameter(parameterURI, parameterID, constrain, constrainPat));
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return parameters;
    }
}
