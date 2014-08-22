package org.aksw.rdfunit.coverage;

import com.hp.hpl.jena.query.*;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Dimitris Kontokostas
 *         Calculates test coverage based on www paper
 * @since 10/8/13 9:06 PM
 */
public class TestCoverageEvaluator {
    private static final Logger log = LoggerFactory.getLogger(TestCoverageEvaluator.class);


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
        StringBuilder inClause = new StringBuilder("");
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

    public void calculateCoverage(QueryExecutionFactory qef, String propertiesFile, String classFile) throws IOException {

        String line;
        long propertiesTotal = 0;
        long classesTotal = 0;

        Map<String, Long> properties = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesFile))) {

            while ((line = br.readLine()) != null) {
                // process the line.
                String[] ar = line.split(" ");
                long count = Long.parseLong(ar[0].trim());
                String ref = ar[1].trim();
                propertiesTotal += count;
                properties.put(ref, count);

            }
        }

        Map<String, Long> classes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(classFile))){

            while ((line = br.readLine()) != null) {
                // process the line.
                String[] ar = line.split(" ");
                long count = Long.parseLong(ar[0].trim());
                String ref = ar[1].trim();
                classesTotal += count;
                classes.put(ref, count);

            }
        }

        calculateCoverage(qef, properties, propertiesTotal, classes, classesTotal);


    }

    public void calculateCoverage(QueryExecutionFactory model, Map<String, Long> propertyCount, long totalProperties, Map<String, Long> classCount, long totalClasses) {

        String sparqlQuery = "";
        Collection<String> references;

        // Fdomain Coverage metric
        references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fDomPatterns)));
        double fDom = getCoverage(references, propertyCount, totalProperties);
        log.info("Fdom Coverage: " + fDom);

        // Frange Coverage metric
        references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fRangPatterns)));
        double fRang = getCoverage(references, propertyCount, totalProperties);
        log.info("fRang Coverage: " + fRang);

        // Fdepend Coverage metric
        references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fDepPatterns)));
        double fDep = getCoverage(references, propertyCount, totalProperties);
        log.info("fDep Coverage: " + fDep);

        // FCard Coverage metric
        references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fCardPatterns)));
        double fCard = getCoverage(references, propertyCount, totalProperties);
        log.info("fCard Coverage: " + fRang);

        // Fmem Coverage metric
        references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fMemPatterns)));
        double fMem = getCoverage(references, classCount, totalClasses);
        log.info("fMem Coverage: " + fMem);

        // FCdep Coverage metric
        references = getReferenceSet(model, sparql.replace("%%PATTERNS%%", generateInClause(fCDepPatterns)));
        double fCDep = getCoverage(references, classCount, totalClasses);
        log.info("fCDep Coverage: " + fCDep);
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
        QueryExecution qe = model.createQueryExecution(q);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution row = rs.next();

            references.add("<" + row.get("reference").toString() + ">");

        }
        qe.close();

        return references;

    }

}
