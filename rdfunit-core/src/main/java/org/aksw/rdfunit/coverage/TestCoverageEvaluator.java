package org.aksw.rdfunit.coverage;

import com.hp.hpl.jena.query.*;
import org.aksw.rdfunit.Utils.DatabuggerUtils;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * User: Dimitris Kontokostas
 * Calculates test coverage based on www paper
 * Created: 10/8/13 9:06 PM
 */
public class TestCoverageEvaluator {
    private static final Logger log = LoggerFactory.getLogger(TestCoverageEvaluator.class);


    private final List<String> fDomPatterns = Arrays.asList("RDFSDOMAIN", "OWLDISJP",
            "TYPRODEP", "OWLSYMMETRICPROP", "OWLASYMMETRICPROP",
            "OWLTRANSPROP", "COMP", "LITRAN", "TYPDEP", "PVT");
    private final List<String> fRangPatterns = Arrays.asList("RDFSRANGE", "OWLDISJP",
            "OWLCARD", "INVFUNC", "OWLSYMMETRICPROP", "OWLASYMMETRICPROP",
            "OWLTRANSPROP", "COMP", "MATCH", "LITRAN", "ONELANG");
    private final List<String> fDepPatterns = Arrays.asList("RDFSRANGE", "RDFSDOMAIN",
            "OWLDISJP", "TYPRODEP", "COMP", "LITRAN", "PVT");
    private final List<String> fCardPatterns = Arrays.asList("OWLCARD", "ONELANG");
    private final List<String> fMemPatterns = Arrays.asList("RDFSRANGE", "RDFSDOMAIN",
            "OWLDISJP", "TYPRODEP", "LITRAN");
    private final List<String> fCDepPatterns = Arrays.asList("OWLDISJC", "TYPDEP");
    private final String sparql = DatabuggerUtils.getAllPrefixes() +
            " SELECT distinct ?reference WHERE {\n" +
            "   ?t a  ruto:TestCase ; \n" +
            "      ruto:basedOnPattern ?pattern ; \n" +
            "      ruto:references ?reference .\n" +
            "   VALUES ( ?pattern )  { %%PATTERNS%%} }";

    private String generateInClause(List<String> patterns) {
        StringBuilder inClause = new StringBuilder("");
        //int count = 0;
        for (String s : patterns) {
            //if (count++ != 0) {
            //    inClause.append(" , ");
            //}
            inClause.append(" ( <")
                    .append(PrefixService.getPrefix("rutp"))
                    .append(s)
                    .append("> ) ");
        }
        return inClause.toString();
    }

    public void calculateCoverage(QueryExecutionFactory model, String propertiesFile, String classFile) throws IOException {


        Map<String, Long> properties = new HashMap<String, Long>();
        BufferedReader br = new BufferedReader(new FileReader(propertiesFile));
        String line;
        long propertiesTotal = 0;
        while ((line = br.readLine()) != null) {
            // process the line.
            String[] ar = line.split(" ");
            long count = Long.parseLong(ar[0].trim());
            String ref = ar[1].trim();
            propertiesTotal += count;
            properties.put(ref, count);

        }
        br.close();


        Map<String, Long> classes = new HashMap<String, Long>();
        br = new BufferedReader(new FileReader(classFile));

        long classesTotal = 0;
        while ((line = br.readLine()) != null) {
            // process the line.
            String[] ar = line.split(" ");
            long count = Long.parseLong(ar[0].trim());
            String ref = ar[1].trim();
            classesTotal += count;
            classes.put(ref, count);

        }
        br.close();

        calculateCoverage(model, properties, propertiesTotal, classes, classesTotal);


    }

    public void calculateCoverage(QueryExecutionFactory model, Map<String, Long> propertyCount, long totalProperties, Map<String, Long> classCount, long totalClasses) {

        String sparqlQuery = "";
        List<String> references;

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

    private double getCoverage(List<String> references, Map<String, Long> referencesCount, long totalReferences) {
        double coverage = 0;

        for (String reference : references) {
            Long count = referencesCount.get(reference);
            if (count != null) {
                coverage += (double) count / (double) totalReferences;
            }
        }

        return coverage;
    }

    private List<String> getReferenceSet(QueryExecutionFactory model, String query) {

        List<String> references = new ArrayList<String>();
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
