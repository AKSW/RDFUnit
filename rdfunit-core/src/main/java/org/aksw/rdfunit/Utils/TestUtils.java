package org.aksw.rdfunit.Utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.exceptions.BindingException;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.io.DataWriter;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.patterns.PatternParameter;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.*;
import org.aksw.rdfunit.tests.results.ResultAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/**
 * @author Dimitris Kontokostas
 *         Various utility test functions for tests
 * @since 9/24/13 10:59 AM
 */
public final class TestUtils {
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    public static java.util.Collection<TestAutoGenerator> instantiateTestGeneratorsFromModel(QueryExecutionFactory queryFactory) {
        java.util.Collection<TestAutoGenerator> autoGenerators = new ArrayList<>();

        String sparqlSelect = PrefixNSService.getSparqlPrefixDecl() +
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
            java.util.Collection<ResultAnnotation> annotations = SparqlUtils.getResultAnnotations(queryFactory, generator);

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

    public static java.util.Collection<TestCase> instantiateTestsFromAG(java.util.Collection<TestAutoGenerator> autoGenerators, Source source) {
        java.util.Collection<TestCase> tests = new ArrayList<>();

        for (TestAutoGenerator tag : autoGenerators) {
            tests.addAll(tag.generate(source));
        }

        return tests;

    }

    public static java.util.Collection<TestCase> instantiateTestsFromModel(Model model) {
        java.util.Collection<TestCase> tests = new ArrayList<>();
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

        // Get all manual tests

        String manualTestsSelectSparql = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?testURI WHERE {" +
                " ?testURI a rut:ManualTestCase }";

        QueryExecution qe = qef.createQueryExecution(manualTestsSelectSparql);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String testURI = qs.get("testURI").toString();
            ManualTestCase tc = instantiateSingleManualTestFromModel(qef, testURI);
            if (tc != null) {
                tests.add(tc);
            }
        }

        // Get all pattern based tests

        String patternTestsSelectSparql = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?testURI WHERE {" +
                " ?testURI a rut:PatternBasedTestCase } ";

        qe = qef.createQueryExecution(patternTestsSelectSparql);
        results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String testURI = qs.get("testURI").toString();
            PatternBasedTestCase tc = instantiateSinglePatternTestFromModel(qef, testURI);
            if (tc != null) {
                tests.add(tc);
            }
        }

        return tests;
    }

    public static ManualTestCase instantiateSingleManualTestFromModel(QueryExecutionFactory qef, String testURI) {

        String sparqlSelect = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?description ?appliesTo ?generated ?source ?sparqlWhere ?sparqlPrevalence ?testGenerator ?testCaseLogLevel WHERE { " +
                " <" + testURI + "> " +
                "    dcterms:description  ?description ;" +
                "    rut:appliesTo        ?appliesTo ;" +
                "    rut:generated        ?generated ;" +
                "    rut:source           ?source ;" +
                "    rut:testCaseLogLevel ?testCaseLogLevel ;" +
                "    rut:sparqlWhere      ?sparqlWhere ;" +
                "    rut:sparqlPrevalence ?sparqlPrevalence ." +
                " OPTIONAL {<" + testURI + ">  rut:testGenerator ?testGenerator .}" +
                "} ";
        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlSelect);
            ResultSet results = qe.execSelect();

            if (results.hasNext()) {
                QuerySolution qs = results.next();

                String description = qs.get("description").toString();
                String appliesTo = qs.get("appliesTo").toString();
                String generated = qs.get("generated").toString();
                String source = qs.get("source").toString();
                RLOGLevel testCaseLogLevel = RLOGLevel.resolve(qs.get("testCaseLogLevel").toString());
                String sparqlWhere = qs.get("sparqlWhere").toString();
                String sparqlPrevalence = qs.get("sparqlPrevalence").toString();
                java.util.Collection<String> referencesLst = getReferencesFromTestCase(qef, testURI);
                String testGenerator = "";
                if (qs.contains("testGenerator")) {
                    testGenerator = qs.get("testGenerator").toString();
                }

                // Get annotations from Test URI
                java.util.Collection<ResultAnnotation> resultAnnotations = SparqlUtils.getResultAnnotations(qef, testURI);

                TestCaseAnnotation annotation =
                        new TestCaseAnnotation(
                                TestGenerationType.resolve(generated),
                                testGenerator,
                                TestAppliesTo.resolve(appliesTo),
                                source,
                                referencesLst,
                                description,
                                testCaseLogLevel,
                                resultAnnotations);

                if (!results.hasNext()) {
                    return new ManualTestCase(
                            testURI,
                            annotation,
                            sparqlWhere,
                            sparqlPrevalence);
                }
            }
        } catch (TestCaseInstantiationException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        log.error("Cannot instantiate test case: " + testURI);
        return null;

    }

    public static PatternBasedTestCase instantiateSinglePatternTestFromModel(QueryExecutionFactory qef, String testURI) {

        String sparqlSelect = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?description ?appliesTo ?generated ?source ?basedOnPattern ?testGenerator ?testCaseLogLevel WHERE { " +
                " <" + testURI + "> " +
                "    dcterms:description ?description ;" +
                "    rut:appliesTo      ?appliesTo ;" +
                "    rut:generated      ?generated ;" +
                "    rut:source         ?source ;" +
                "    rut:testCaseLogLevel ?testCaseLogLevel ;" +
                "    rut:basedOnPattern ?basedOnPattern ;" +
                " OPTIONAL {<" + testURI + ">  rut:testGenerator ?testGenerator .}" +
                "} ";

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlSelect);
            ResultSet results = qe.execSelect();

            if (results.hasNext()) {
                QuerySolution qs = results.next();

                String description = qs.get("description").toString();
                String appliesTo = qs.get("appliesTo").toString();
                String generated = qs.get("generated").toString();
                String source = qs.get("source").toString();
                RLOGLevel testCaseLogLevel = RLOGLevel.resolve(qs.get("testCaseLogLevel").toString());
                String patternURI = qs.get("basedOnPattern").toString();
                Pattern pattern = PatternService.getPattern(patternURI.replace(PrefixNSService.getNSFromPrefix("rutp"), ""));
                if (pattern == null) {
                    log.error("Pattern does not exists for test: " + testURI);
                    return null;
                }

                java.util.Collection<String> referencesLst = getReferencesFromTestCase(qef, testURI);
                java.util.Collection<Binding> bindings = getBindingsFromTestCase(qef, testURI, pattern);
                String testGenerator = "";
                if (qs.contains("testGenerator")) {
                    testGenerator = qs.get("testGenerator").toString();
                }

                // Get annotations from Test URI
                java.util.Collection<ResultAnnotation> resultAnnotations = SparqlUtils.getResultAnnotations(qef, testURI);

                TestCaseAnnotation annotation =
                        new TestCaseAnnotation(
                                TestGenerationType.resolve(generated),
                                testGenerator,
                                TestAppliesTo.resolve(appliesTo),
                                source,
                                referencesLst,
                                description,
                                testCaseLogLevel,
                                resultAnnotations);

                if (!results.hasNext()) {
                    return new PatternBasedTestCase(
                            testURI,
                            annotation,
                            pattern,
                            bindings);
                }
            }
        } catch (TestCaseInstantiationException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        log.error("Cannot instantiate test case: " + testURI);
        return null;
    }

    public static void writeTestsToFile(java.util.Collection<TestCase> tests, DataWriter testCache) {
        Model model = ModelFactory.createDefaultModel();
        for (TestCase t : tests) {
            t.serialize(model);
        }
        try {
            PrefixNSService.setNSPrefixesInModel(model);
            testCache.write(model);
        } catch (TripleWriterException e) {
            log.error("Cannot cache tests: " + e.getMessage());
        }
    }

    public static java.util.Collection<String> getReferencesFromTestCase(QueryExecutionFactory qef, String testURI) {

        java.util.Collection<String> references = new ArrayList<>();

        String sparqlReferencesSelect = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?references WHERE { " +
                " <" + testURI + "> rut:references ?references . }";

        QueryExecution qe = null;
        try {
            qe = qef.createQueryExecution(sparqlReferencesSelect);
            ResultSet results = qe.execSelect();

            while (results.hasNext()) {
                QuerySolution qs = results.next();
                references.add(qs.get("references").toString());
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }
        return references;
    }

    public static java.util.Collection<Binding> getBindingsFromTestCase(QueryExecutionFactory qef, String testURI, Pattern pattern) {

        java.util.Collection<Binding> bindings = new ArrayList<>();

        String sparqlReferencesSelect = PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?parameter ?value WHERE { " +
                " <" + testURI + "> rut:binding ?binding ." +
                " ?binding rut:bindingValue ?value ;" +
                "          rut:parameter ?parameter }";

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
            if (qe != null) {
                qe.close();
            }
        }
        return bindings;
    }

    public static String generateTestURI(String sourcePrefix, Pattern pattern, java.util.Collection<Binding> bindings, String generatorURI) {
        String testURI = PrefixNSService.getNSFromPrefix("rutt") + sourcePrefix + "-" + pattern.getId() + "-";
        String string2hash = generatorURI;
        for (Binding binding : bindings) {
            string2hash += binding.getValue();
        }
        String md5Hash = TestUtils.getMD5FromString(string2hash);
        if (md5Hash == null) {
            testURI += JenaUUID.generate().asString();
        } else {
            testURI += md5Hash;
        }
        return testURI;
    }

    // Taken from http://stackoverflow.com/questions/415953/generate-md5-hash-in-java
    public static String getMD5FromString(String md5) {
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
