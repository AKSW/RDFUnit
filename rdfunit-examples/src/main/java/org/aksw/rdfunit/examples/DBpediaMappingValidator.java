package org.aksw.rdfunit.examples;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.*;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceFactory;
import org.aksw.rdfunit.utils.TestUtils;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Does validation of the DBpedia mappings
 *
 * @author Dimitris Kontokostas
 * @since 6/30/15 4:15 PM
 * @version $Id: $Id
 */
public class DBpediaMappingValidator {

    private static final String mappingServer = "http://mappings.dbpedia.org"; // "http://localhost:9999";

    private static final String dbpediaOntology = mappingServer + "/server/ontology/dbpedia.owl";
    private static final String rmlManualTests = "/org/aksw/rdfunit/tests/Manual/www.w3.org/ns/r2rml/rr.tests.Manual.ttl";
    private static final Collection<String> languages = Arrays.asList("ar", "az", "be", "bg", "bn", "ca", "commons", "cs", "cy", "de", "el", "en", "eo", "es", "et", "eu", "fr", "ga", "hi", "hr", "hu", "hy", "id", "it", "ja", "ko", "nl", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "tr", "uk", "ur", "zh");
    // 2014 languages
    // Arrays.asList("ar", "be", "bg", "bn", "ca", "commons", "cs", "cy", "de", "el", "en", "eo", "es", "et", "eu", "fr", "ga", "hi", "hr", "hu", "id", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sk", "sl", "sr", "tr", "ur", "zh");
    // Arrays.asList("el", "bg");

    private static final String sparqlQuery = PrefixNSService.getSparqlPrefixDecl() +
            " select ?error ?missing ?predicate ?mapping\n" +
            " where {\n" +
            "  ?v a sh:ValidationResult ;\n" +
            "     rut:testCase rutt:rr-predicateObjectMap-wrong-domain ;\n" +
            "     rdf:predicate ?predicate ;\n" +
            "     sh:expectedObject ?missing ;\n" +
            "     sh:object ?error ;\n" +
            "     rlog:resource ?mapping .\n" +
            " } ORDER BY ?mapping ";


    private String getRMLlink(String lang) {
        return mappingServer + "/server/mappings/" + lang + "/pages/rdf/all";
    }

    private RdfReader getRMLReader() {
        Collection<RdfReader> allReaders = new ArrayList<>();
        // we add the ontology
        allReaders.add(new RdfDereferenceReader(dbpediaOntology));
        // add all the mapping languages
        allReaders.addAll(
                languages.stream()
                        .map(lang -> new RdfDereferenceReader(getRMLlink(lang)))
                        .collect(Collectors.toList()));
        return new RdfMultipleReader(allReaders);
    }

    private TestSource getMappingSource() {
        return TestSourceFactory.createDumpTestSource("dbp-mappings", "http://mappings.dbpedia.org", getRMLReader(), new ArrayList<>());

    }

    private TestSuite getDBpMappingsTestSuite() throws RdfReaderException {
        Collection<TestCase> tests = TestUtils.instantiateTestsFromModel(
                RdfReaderFactory.createResourceReader(rmlManualTests).read());

        return new TestSuite(tests);
    }

    /**
     * <p>validateAllMappings.</p>
     *
     * @return a {@link org.apache.jena.rdf.model.Model} object.
     * @throws RdfReaderException if any.
     */
    public TestExecution validateAllMappings() throws RdfReaderException {
        return RDFUnitStaticValidator.validate(TestCaseExecutionType.shaclFullTestCaseResult, getMappingSource(), getDBpMappingsTestSuite());
    }

    /**
     * <p>getErrorListFromModel.</p>
     *
     * @param model a {@link org.apache.jena.rdf.model.Model} object.
     * @return a {@link java.util.List} object.
     */
    public List<MappingDomainError> getErrorListFromModel(Model model) {
        List<MappingDomainError> mappingDomainErrors = new ArrayList<>();
        try  ( QueryExecution qe = QueryExecutionFactory.create(sparqlQuery, model))
        {

            qe.execSelect().forEachRemaining( qs -> {

                String mapping = qs.get("mapping").toString();
                String error = qs.get("error").toString();
                String missing = qs.get("missing").toString();
                String predicate = qs.get("predicate").toString();

                mappingDomainErrors.add(new MappingDomainError(mapping, predicate, error, missing));

            } );
        }
        return mappingDomainErrors;
    }

    /**
     * <p>getErrorsAsMap.</p>
     *
     * @param mappingDomainErrors a {@link java.util.Collection} object.
     * @return a {@link java.util.Map} object.
     */
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

    /**
     * <p>convertToJson.</p>
     *
     * @param errorLangMap a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
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


    /**
     * <p>validateAndGetJson.</p>
     *
     * @return a {@link java.lang.String} object.
     * @throws RdfReaderException if any.
     */
    public String validateAndGetJson() throws RdfReaderException {
        Model model = ModelFactory.createDefaultModel();
        TestExecutionWriter.create(validateAllMappings()).write(model);

        //new RDFFileWriter("mappings.ttl").write(model);
        List<MappingDomainError> errors = getErrorListFromModel(model);


        return convertToJson(getErrorsAsMap(errors));
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {


        if (args.length > 1) {
            throw new IllegalArgumentException("Expected at most one argument - the folder");
        }

        // get folder name
        String folder = "./";
        if (args.length == 1) {
            folder = args[0];
            if (!folder.endsWith("/")) {
                 folder = folder + "/";
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        final String filenameDefault = folder + "errors.json";
        final String filenameArchive = folder + "/archive/errors." + timeStamp + ".json";
        // create folder if it does not exists
        File archivedFolder = new File(folder+"/archive/");
        boolean dirsCreated = archivedFolder.mkdirs();
        if (!dirsCreated) {
            System.err.println("could not create archive folder");
        }

        final String json = new DBpediaMappingValidator().validateAndGetJson();

        //write json to filenameDefault
        try (Writer outDeafault = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filenameDefault), "UTF-8"));
             Writer outArchive = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filenameArchive), "UTF-8"))
        ) {
            outDeafault.write(json);
            outArchive.write(json);
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

