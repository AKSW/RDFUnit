package org.aksw.rdfunit.examples;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.*;
import org.aksw.rdfunit.sources.DumpTestSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.results.ExtendedTestCaseResult;
import org.aksw.rdfunit.utils.PrefixNSService;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Does validation of the DBpedia mappings
 *
 * @author Dimitris Kontokostas
 * @since 6/30/15 4:15 PM
 */
public class DBpediaMappingValidator {

    private static final String mappingServer = "http://mappings.dbpedia.org";
    //private static final String mappingServer = "http://localhost:9999";

    private static final String dbpediaOntology = mappingServer + "/server/ontology/dbpedia.owl";
    private static final String rmlManualTests = "/org/aksw/rdfunit/tests/Manual/www.w3.org/ns/r2rml/rr.tests.Manual.ttl";
    //private static final Collection<String> languages = Arrays.asList("ar", "az", "be", "bg", "bn", "ca", "commons", "cs", "cy", "de", "el", "en", "eo", "es", "et", "eu", "fr", "ga", "hi", "hr", "hu", "hy", "id", "it", "ja", "ko", "nl", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "tr", "uk", "ur", "zh");
    // 2014 languages
    private static final Collection<String> languages = Arrays.asList("ar", "be", "bg", "bn", "ca", "commons", "cs", "cy", "de", "el", "en", "eo", "es", "et", "eu", "fr", "ga", "hi", "hr", "hu", "id", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sk", "sl", "sr", "tr", "ur", "zh");
    //private static final Collection<String> languages = Arrays.asList("el", "bg");

    private static final String sparqlQuery = PrefixNSService.getSparqlPrefixDecl() +
            " select ?error ?missing ?predicate ?mapping\n" +
            " where {\n" +
            "  ?v a rut:ExtendedTestCaseResult ;\n" +
            "     rut:testCase rutt:rr-predicateObjectMap-wrong-domain ;\n" +
            "     rdf:predicate ?predicate ;\n" +
            "     spin:missingValue ?missing ;\n" +
            "     spin:violationValue ?error ;\n" +
            "     rlog:resource ?mapping .\n" +
            " } ORDER BY ?mapping ";


    private String getRMLlink(String lang) {
        return mappingServer + "/server/mappings/" + lang + "/pages/rdf/all";
    }

    private RDFReader getRMLReader() {
        Collection<RDFReader> allReaders = new ArrayList<>();
        // we add the ontology
        allReaders.add(new RDFDereferenceReader(dbpediaOntology));
        // add all the mapping languages
        for (String lang : languages) {
            allReaders.add(new RDFDereferenceReader(getRMLlink(lang)));
        }
        return new RDFMultipleReader(allReaders);
    }

    private TestSource getMappingSource() {
        return new DumpTestSource("dbp-mappings", "http://mappings.dbpedia.org", getRMLReader(), Collections.EMPTY_LIST);

    }

    private TestSuite getDBpMappingsTestSuite() throws RDFReaderException {
        Collection<TestCase> tests = TestUtils.instantiateTestsFromModel(
                RDFReaderFactory.createResourceReader(rmlManualTests).read());

        return new TestSuite(tests);
    }

    public Model validateAllMappings() throws RDFReaderException {
        return RDFUnitStaticWrapper.validate(TestCaseExecutionType.extendedTestCaseResult, getMappingSource(), getDBpMappingsTestSuite());
    }

    public List<MappingDomainError> getErrorListFromModel(Model model) {
        QueryExecution qe = null;
        List<MappingDomainError> mappingDomainErrors = new ArrayList<>();
        try {
            qe = QueryExecutionFactory.create(sparqlQuery, model);

            ResultSet results = qe.execSelect();

            ExtendedTestCaseResult result = null;

            while (results.hasNext()) {

                QuerySolution qs = results.next();

                String mapping = qs.get("mapping").toString();
                String error = qs.get("error").toString();
                String missing = qs.get("missing").toString();
                String predicate = qs.get("predicate").toString();

                mappingDomainErrors.add(new MappingDomainError(mapping, predicate, error, missing));

            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
        return mappingDomainErrors;
    }

    public Map<String, List<MappingDomainError>> getErrorsAsMap(Collection<MappingDomainError> mappingDomainErrors) {
        Map<String, List<MappingDomainError>> errorsAsMap = new HashMap<>();
        for (MappingDomainError error : mappingDomainErrors) {
            List<MappingDomainError> langErrors = errorsAsMap.get(error.language);
            if (langErrors == null) {
                langErrors = new ArrayList<>();
                errorsAsMap.put(error.language, langErrors);
            }
            langErrors.add(error);

        }

        return errorsAsMap;
    }

    public String convertToJson(Map<String, List<MappingDomainError>> errorLangMap) {
        StringBuilder builder = new StringBuilder();

        builder.append('{');

        for (Map.Entry<String, List<MappingDomainError>> entry : errorLangMap.entrySet()) {
            builder.append("\"").append(entry.getKey()).append("\"");
            builder.append(": [ ");

            for (MappingDomainError error : entry.getValue()) {
                builder.append("{");
                builder.append("\"mapping\":").append("\"").append(error.mapping).append("\",");
                builder.append("\"predicate\":").append("\"").append(error.predicate).append("\",");
                builder.append("\"expected\":").append("\"").append(error.expected).append("\",");
                builder.append("\"existing\":").append("\"").append(error.wrong).append("\"");
                builder.append("},");
            }
            builder.deleteCharAt(builder.length() - 1);  // delete last ','

            builder.append("],");


        }
        builder.deleteCharAt(builder.length() - 1);  // delete last ','


        builder.append('}');

        return builder.toString();
    }


    public String validateAndGetJson() throws RDFReaderException {
        Model model = validateAllMappings();

        //new RDFFileWriter("mappings.ttl").write(model);
        List<MappingDomainError> errors = getErrorListFromModel(model);


        return convertToJson(getErrorsAsMap(errors));
    }

    public static void main(String[] args) throws Exception {


        if (args.length > 1) {
            throw new RuntimeException("Expected at most one argument - the folder");
        }

        // get folder name
        String folder = "";
        if (args.length == 1) {
            folder = args[0];
        }
        final String filename = folder + "errors.json";

        //write json to filename
        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), "UTF-8"));) {
            out.write(new DBpediaMappingValidator().validateAndGetJson());
        }

    }

    static class MappingDomainError {
        public final String language;
        public final String mapping;
        public final String predicate;
        public final String wrong;
        public final String expected;

        MappingDomainError(String mappingUri, String predicate, String wrong, String expected) {
            String tmpStr = mappingUri.replace("http://mappings.dbpedia.org/server/mappings/", "");
            int endOfLang = tmpStr.indexOf('/');
            this.language = tmpStr.substring(0, endOfLang);
            int endOfMap = tmpStr.indexOf("/Class");
            this.mapping = tmpStr.substring(endOfLang + 1, endOfMap);


            this.predicate = predicate;
            this.wrong = wrong;
            this.expected = expected;

        }

    }
}

