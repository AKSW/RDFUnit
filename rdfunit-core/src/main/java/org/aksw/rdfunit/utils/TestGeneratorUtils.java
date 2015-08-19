package org.aksw.rdfunit.utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.tests.TestAutoGenerator;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.ResultAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;


/**
 * <p>TestUtils class.</p>
 *
 * @author Dimitris Kontokostas
 *         Various utility test functions for tests
 * @since 9/24/13 10:59 AM
 * @version $Id: $Id
 */
public final class TestGeneratorUtils {
    private static final Logger log = LoggerFactory.getLogger(TestGeneratorUtils.class);

    private TestGeneratorUtils() {
    }


    /**
     * <p>instantiateTestGeneratorsFromModel.</p>
     *
     * @param queryFactory a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<TestAutoGenerator> instantiateTestGeneratorsFromModel(QueryExecutionFactory queryFactory) {
        Collection<TestAutoGenerator> autoGenerators = new ArrayList<>();

        String sparqlSelect = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
                " SELECT ?generator ?desc ?query ?patternID WHERE { " +
                " ?generator " +
                "    a rut:TestGenerator ; " +
                "    dcterms:description ?desc ; " +
                "    rut:sparqlGenerator ?query ; " +
                "    rut:basedOnPattern ?pattern . " +
                " ?pattern dcterms:identifier ?patternID ." +
                "} ";

        QueryExecution qe = queryFactory.createQueryExecution(sparqlSelect);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();

            String generator = qs.get("generator").toString();
            String description = qs.get("desc").toString();
            String query = qs.get("query").toString();
            String patternID = qs.get("patternID").toString();

            // Get annotations from TAG URI
            Collection<ResultAnnotation> annotations = SparqlUtils.getResultAnnotations(queryFactory, generator);

            TestAutoGenerator tag = new TestAutoGenerator(generator, description, query, PatternService.getPattern(patternID), annotations);
            if (tag.isValid()) {
                autoGenerators.add(tag);
            } else {
                log.error("AutoGenerator not valid: " + tag.getUri());
                System.exit(-1);
            }
        }
        qe.close();

        return autoGenerators;

    }

    /**
     * <p>instantiateTestsFromAG.</p>
     *
     * @param autoGenerators a {@link java.util.Collection} object.
     * @param source a {@link org.aksw.rdfunit.sources.Source} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<TestCase> instantiateTestsFromAG(Collection<TestAutoGenerator> autoGenerators, SchemaSource source) {
        Collection<TestCase> tests = new ArrayList<>();

        for (TestAutoGenerator tag : autoGenerators) {
            tests.addAll(tag.generate(source));
        }

        return tests;

    }
}
