package org.aksw.rdfunit.coverage;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.statistics.DatasetStatisticsClassesCount;
import org.aksw.rdfunit.statistics.DatasetStatisticsPropertiesCount;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * <p>TestCoverageEvaluator class.</p>
 *
 * @author Dimitris Kontokostas
 *         Calculates test coverage based on www paper
 * @since 10/8/13 9:06 PM
 * @version $Id: $Id
 */
public class TestCoverageEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCoverageEvaluator.class);


    private final Collection<String> fDomPatterns = Arrays.asList("RDFSDOMAIN", "OWLDISJP",
            "TYPRODEP", "OWLSYMMETRICPROP", "OWLASYMMETRICPROP",
            "OWLTRANSPROP", "COMP", "LITRAN", "TYPDEP", "PVT");
    private final Collection<String> fRangPatterns = Arrays.asList("RDFSRANGE", "OWLDISJP",
            "OWLCARD", "INVFUNC", "OWLSYMMETRICPROP", "OWLASYMMETRICPROP",
            "OWLTRANSPROP", "COMP", "MATCH", "LITRAN", "ONELANG");
    private final Collection<String> fDepPatterns = Arrays.asList("RDFSRANGE", "RDFSDOMAIN",
            "OWLDISJP", "TYPRODEP", "COMP", "LITRAN", "PVT");
    private final Collection<String> fCardPatterns = Arrays.asList("OWLCARD", "ONELANG");
    private final Collection<String> fMemPatterns = Arrays.asList("RDFSRANGE", "RDFSDOMAIN",
            "OWLDISJP", "TYPRODEP", "LITRAN");
    private final Collection<String> fCDepPatterns = Arrays.asList("OWLDISJC", "TYPDEP");
    private final String sparql = PrefixNSService.getSparqlPrefixDecl() +
            " SELECT distinct ?reference WHERE {\n" +
            "   ?t a  rut:TestCase ; \n" +
            "      rut:basedOnPattern ?pattern ; \n" +
            "      rut:references ?reference .\n" +
            "   VALUES ( ?pattern )  { %%PATTERNS%%} }";

    private String generateInClause(Collection<String> patterns) {
        StringBuilder inClause = new StringBuilder();
        //int count = 0;
        for (String s : patterns) {
            //if (count++ != 0) {
            //    inClause.append(" , ");
            //}
            inClause.append(" ( <")
                    .append(PrefixNSService.getNSFromPrefix("rutp"))
                    .append(s)
                    .append("> ) ");
        }
        return inClause.toString();
    }

    /**
     * <p>calculateCoverage.</p>
     *
     * @param testSuiteQEF a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @throws java.io.IOException if any.
     * @param inputSource a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    public void calculateCoverage(QueryExecutionFactory testSuiteQEF, QueryExecutionFactory inputSource) throws IOException {

        Map<String, Long> properties = new DatasetStatisticsPropertiesCount().getStatisticsMap(inputSource);
        long propertiesTotal = properties.size();

        Map<String, Long> classes = new DatasetStatisticsClassesCount().getStatisticsMap(inputSource);
        long classesTotal = classes.size();

        calculateCoverage(testSuiteQEF, properties, propertiesTotal, classes, classesTotal);
    }

    private void calculateCoverage(QueryExecutionFactory model, Map<String, Long> propertyCount, long totalProperties, Map<String, Long> classCount, long totalClasses) {

        Collection<String> references;

        if (totalProperties > 0) {
            // Fdomain Coverage metric
            references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fDomPatterns)));
            double fDom = getCoverage(references, propertyCount, totalProperties);
            LOGGER.info("Fdom Coverage: {}", fDom);

            // Frange Coverage metric
            references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fRangPatterns)));
            double fRang = getCoverage(references, propertyCount, totalProperties);
            LOGGER.info("fRang Coverage: {}", fRang);

            // Fdepend Coverage metric
            references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fDepPatterns)));
            double fDep = getCoverage(references, propertyCount, totalProperties);
            LOGGER.info("fDep Coverage: {}", fDep);

            // FCard Coverage metric
            references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fCardPatterns)));
            double fCard = getCoverage(references, propertyCount, totalProperties);
            LOGGER.info("fCard Coverage: {}", fCard);
        } else {
            LOGGER.warn("No properties found in Source (probably source is empty)");
        }

        if (totalClasses > 0) {
            // Fmem Coverage metric
            references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fMemPatterns)));
            double fMem = getCoverage(references, classCount, totalClasses);
            LOGGER.info("fMem Coverage: {}", fMem);

            // FCdep Coverage metric
            references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fCDepPatterns)));
            double fCDep = getCoverage(references, classCount, totalClasses);
            LOGGER.info("fCDep Coverage: {}", fCDep);
        } else {
            LOGGER.warn("No Class usage found in Source");
        }
    }

    private double getCoverage(Collection<String> references, Map<String, Long> referencesCount, long totalReferences) {
        double coverage = 0;

        for (String reference : references) {
            Long count = referencesCount.get(reference);
            if (count != null) {
                coverage += (double) count / (double) totalReferences;
            }
        }

        return coverage;
    }

    private Collection<String> getReferenceSet(QueryExecutionFactory model, String query) {

        Collection<String> references = new ArrayList<>();
        Query q = QueryFactory.create(query);
        try (QueryExecution qe = model.createQueryExecution(q))
        {
            qe.execSelect().forEachRemaining(
                    row -> references.add("<" + row.get("reference").toString() + ">")
            );
        }

        return references;

    }

}
