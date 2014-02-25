package org.aksw.databugger.Utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.enums.TestGenerationType;
import org.aksw.databugger.exceptions.BindingException;
import org.aksw.databugger.exceptions.TestCaseException;
import org.aksw.databugger.exceptions.TripleWriterException;
import org.aksw.databugger.io.TripleWriter;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.services.PatternService;
import org.aksw.databugger.services.PrefixService;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.*;
import org.aksw.databugger.tests.results.ResultAnnotation;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Various utility test functions for tests
 * Created: 9/24/13 10:59 AM
 */
public class TestUtils {
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    public static List<TestAutoGenerator> instantiateTestGeneratorsFromModel(QueryExecutionFactory queryFactory) {
        List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

        String sparqlSelect = DatabuggerUtils.getAllPrefixes() +
                " SELECT ?generator ?desc ?query ?patternID WHERE { " +
                " ?generator " +
                "    a tddo:TestGenerator ; " +
                "    dcterms:description ?desc ; " +
                "    tddo:sparqlGenerator ?query ; " +
                "    tddo:basedOnPattern ?pattern . " +
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
            List<ResultAnnotation> annotations = SparqlUtils.getResultAnnotations(queryFactory, generator);

            TestAutoGenerator tag = new TestAutoGenerator(generator, description, query, PatternService.getPattern(patternID), annotations);
            if (tag.isValid())
                autoGenerators.add(tag);
            else {
                log.error("AutoGenerator not valid: " + tag.getURI());
                System.exit(-1);
            }
        }
        qe.close();

        return autoGenerators;

    }

    public static List<TestCase> instantiateTestsFromAG(List<TestAutoGenerator> autoGenerators, Source source) {
        List<TestCase> tests = new ArrayList<TestCase>();

        for (TestAutoGenerator tag : autoGenerators) {
            tests.addAll(tag.generate(source));
        }

        return tests;

    }

    public static List<TestCase> instantiateTestsFromModel(Model model) {
        List<TestCase> tests = new ArrayList<TestCase>();
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

        // Get all manual tests

        String manualTestsSelectSparql = DatabuggerUtils.getAllPrefixes() +
                " SELECT DISTINCT ?testURI WHERE {" +
                " ?testURI a tddo:ManualTestCase }";

        QueryExecution qe = qef.createQueryExecution(manualTestsSelectSparql);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String testURI = qs.get("testURI").toString();
            ManualTestCase tc = instantiateSingleManualTestFromModel(qef, testURI);
            if (tc != null)
                tests.add(tc);
        }

        // Get all pattern based tests

        String patternTestsSelectSparql = DatabuggerUtils.getAllPrefixes() +
                " SELECT DISTINCT ?testURI WHERE {" +
                " ?testURI a tddo:PatternBasedTestCase } ";

        qe = qef.createQueryExecution(patternTestsSelectSparql);
        results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String testURI = qs.get("testURI").toString();
            PatternBasedTestCase tc = instantiateSinglePatternTestFromModel(qef, testURI);
            if (tc != null)
                tests.add(tc);
        }

        return tests;
    }

    public static ManualTestCase instantiateSingleManualTestFromModel(QueryExecutionFactory qef, String testURI) {

        String sparqlSelect = DatabuggerUtils.getAllPrefixes() +
                " SELECT DISTINCT ?appliesTo ?generated ?source ?sparqlWhere ?sparqlPrevalence ?testGenerator ?testCaseLogLevel WHERE { " +
                " <" + testURI + "> " +
                "    tddo:appliesTo        ?appliesTo ;" +
                "    tddo:generated        ?generated ;" +
                "    tddo:source           ?source ;" +
                "    tddo:testCaseLogLevel ?testCaseLogLevel ;" +
                "    tddo:sparqlWhere      ?sparqlWhere ;" +
                "    tddo:sparqlPrevalence ?sparqlPrevalence ." +
                " OPTIONAL {<" + testURI + ">  tddo:testGenerator ?testGenerator .}" +
                "} ";
        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlSelect);
            ResultSet results = qe.execSelect();

            if (results.hasNext()) {
                QuerySolution qs = results.next();

                String appliesTo = qs.get("appliesTo").toString();
                String generated = qs.get("generated").toString();
                String source = qs.get("source").toString();
                String testCaseLogLevel = qs.get("testCaseLogLevel").toString();
                String sparqlWhere = qs.get("sparqlWhere").toString();
                String sparqlPrevalence = qs.get("sparqlPrevalence").toString();
                List<String> referencesLst = getReferencesFromTestCase(qef, testURI);
                String testGenerator = "";
                if (qs.contains("testGenerator"))
                    testGenerator = qs.get("testGenerator").toString();

                // Get annotations from Test URI
                List<ResultAnnotation> resultAnnotations = SparqlUtils.getResultAnnotations(qef, testURI);

                TestCaseAnnotation annotation =
                        new TestCaseAnnotation(
                                TestGenerationType.resolve(generated),
                                testGenerator,
                                TestAppliesTo.resolve(appliesTo),
                                source,
                                referencesLst,
                                testCaseLogLevel,
                                resultAnnotations);

                if (!results.hasNext())
                    return new ManualTestCase(
                            testURI,
                            annotation,
                            sparqlWhere,
                            sparqlPrevalence);
            }
        } catch (TestCaseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null)
                qe.close();
        }

        log.error("Cannot instantiate test case: " + testURI);
        return null;

    }

    public static PatternBasedTestCase instantiateSinglePatternTestFromModel(QueryExecutionFactory qef, String testURI) {

        String sparqlSelect = DatabuggerUtils.getAllPrefixes() +
                " SELECT DISTINCT ?appliesTo ?generated ?source ?basedOnPattern ?testGenerator ?testCaseLogLevel WHERE { " +
                " <" + testURI + "> " +
                "    tddo:appliesTo      ?appliesTo ;" +
                "    tddo:generated      ?generated ;" +
                "    tddo:source         ?source ;" +
                "    tddo:testCaseLogLevel ?testCaseLogLevel ;" +
                "    tddo:basedOnPattern ?basedOnPattern ;" +
                " OPTIONAL {<" + testURI + ">  tddo:testGenerator ?testGenerator .}" +
                "} ";

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlSelect);
            ResultSet results = qe.execSelect();

            if (results.hasNext()) {
                QuerySolution qs = results.next();

                String appliesTo = qs.get("appliesTo").toString();
                String generated = qs.get("generated").toString();
                String source = qs.get("source").toString();
                String testCaseLogLevel = qs.get("testCaseLogLevel").toString();
                String patternURI = qs.get("basedOnPattern").toString();
                Pattern pattern = PatternService.getPattern(patternURI.replace(PrefixService.getPrefix("tddp"), ""));
                if (pattern == null) {
                    log.error("Pattern does not exists for test: " + testURI);
                    return null;
                }

                List<String> referencesLst = getReferencesFromTestCase(qef, testURI);
                List<Binding> bindings = getBindingsFromTestCase(qef, testURI, pattern);
                String testGenerator = "";
                if (qs.contains("testGenerator"))
                    testGenerator = qs.get("testGenerator").toString();

                // Get annotations from Test URI
                List<ResultAnnotation> resultAnnotations = SparqlUtils.getResultAnnotations(qef, testURI);

                TestCaseAnnotation annotation =
                        new TestCaseAnnotation(
                                TestGenerationType.resolve(generated),
                                testGenerator,
                                TestAppliesTo.resolve(appliesTo),
                                source,
                                referencesLst,
                                testCaseLogLevel,
                                resultAnnotations);

                if (!results.hasNext())
                    return new PatternBasedTestCase(
                            testURI,
                            annotation,
                            pattern,
                            bindings);
            }
        } catch (TestCaseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (qe != null)
                qe.close();
        }

        log.error("Cannot instantiate test case: " + testURI);
        return null;
    }

    public static void writeTestsToFile(List<TestCase> tests, TripleWriter testCache) {
        Model model = ModelFactory.createDefaultModel();
        for (TestCase t : tests)
            t.serialize(model);
        try {
            model.setNsPrefixes(PrefixService.getPrefixMap());
            testCache.write(model);
        } catch (TripleWriterException e) {
            log.error("Cannot cache tests: " + e.getMessage());
        }
    }

    public static List<String> getReferencesFromTestCase(QueryExecutionFactory qef, String testURI) {

        List<String> references = new ArrayList<String>();

        String sparqlReferencesSelect = DatabuggerUtils.getAllPrefixes() +
                " SELECT DISTINCT ?references WHERE { " +
                " <" + testURI + "> tddo:references ?references . }";

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlReferencesSelect);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();
                references.add(qs.get("references").toString());
            }
        } finally {
            if (qe != null)
                qe.close();
        }
        return references;
    }

    public static List<Binding> getBindingsFromTestCase(QueryExecutionFactory qef, String testURI, Pattern pattern) {

        List<Binding> bindings = new ArrayList<Binding>();

        String sparqlReferencesSelect = DatabuggerUtils.getAllPrefixes() +
                " SELECT DISTINCT ?parameter ?value WHERE { " +
                " <" + testURI + "> tddo:binding ?binding ." +
                " ?binding tddo:bindingValue ?value ;" +
                "          tddo:parameter ?parameter }";

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlReferencesSelect);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();

                String parameterURI = qs.get("parameter").toString();
                PatternParameter parameter = pattern.getParameter(parameterURI);
                if (parameter == null) {
                    log.error("Test instantiation error: Pattern " + pattern.getId() + " does not contain parameter " + parameterURI + " in TestCase: " + testURI);
                    continue;
                }

                RDFNode value = qs.get("value");

                try {
                    bindings.add(new Binding(parameter, value));
                } catch (BindingException e) {
                    log.error("Non valid binding for parameter " + parameter.getId() + " in Test: " + testURI);
                }
            }
        } finally {
            if (qe != null)
                qe.close();
        }
        return bindings;
    }

    public static String generateTestURI(String sourcePrefix, Pattern pattern, List<Binding> bindings, String generatorURI) {
        String testURI = PrefixService.getPrefix("tddt") + sourcePrefix + "-" + pattern.getId() + "-";
        String string2hash = generatorURI;
        for (Binding binding : bindings)
            string2hash += binding.getValue();
        String md5Hash = TestUtils.MD5(string2hash);
        if (md5Hash == null)
            testURI += JenaUUID.generate().asString();
        else
            testURI += md5Hash;
        return testURI;
    }

    // Taken from http://stackoverflow.com/questions/415953/generate-md5-hash-in-java
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
