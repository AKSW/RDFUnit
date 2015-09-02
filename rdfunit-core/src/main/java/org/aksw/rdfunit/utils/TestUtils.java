package org.aksw.rdfunit.utils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.elements.implementations.ManualTestCaseImpl;
import org.aksw.rdfunit.elements.implementations.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.elements.interfaces.Pattern;
import org.aksw.rdfunit.elements.interfaces.PatternParameter;
import org.aksw.rdfunit.elements.interfaces.ResultAnnotation;
import org.aksw.rdfunit.elements.interfaces.TestCase;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.exceptions.BindingException;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.io.writer.RDFWriter;
import org.aksw.rdfunit.io.writer.RDFWriterException;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.tests.Binding;
import org.aksw.rdfunit.tests.TestCaseAnnotation;
import org.aksw.rdfunit.tests.TestCaseValidator;
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
public final class TestUtils {
    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    /**
     * <p>instantiateTestsFromModel.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<TestCase> instantiateTestsFromModel(Model model) {
        try {
            return instantiateTestsFromModel(model, false);
        } catch (TestCaseInstantiationException e) {
            // This should not occur since we pass strict-> false
        }
        throw new RuntimeException("Unexpected exception...");
    }

    /**
     * <p>instantiateTestsFromModel.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @param strict a boolean.
     * @return a {@link java.util.Collection} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public static Collection<TestCase> instantiateTestsFromModel(Model model, boolean strict) throws TestCaseInstantiationException {
        Collection<TestCase> tests = new ArrayList<>();
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(model);

        // Get all manual tests

        String manualTestsSelectSparql = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?testURI WHERE {" +
                " ?testURI a rut:ManualTestCase }";

        QueryExecution qe = qef.createQueryExecution(manualTestsSelectSparql);
        ResultSet results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String testURI = qs.get("testURI").toString();
            try {
                ManualTestCaseImpl tc = instantiateSingleManualTestFromModel(qef, testURI);
                TestCaseValidator validator = new TestCaseValidator(tc);
                validator.validate();
                tests.add(tc);
            } catch (TestCaseInstantiationException e) {
                log.error(e.getMessage(), e);
                if (strict) {
                    throw new TestCaseInstantiationException(e.getMessage(), e);
                }
            }

        }

        // Get all pattern based tests

        String patternTestsSelectSparql = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
                " SELECT DISTINCT ?testURI WHERE {" +
                " ?testURI a rut:PatternBasedTestCase } ";

        qe = qef.createQueryExecution(patternTestsSelectSparql);
        results = qe.execSelect();

        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String testURI = qs.get("testURI").toString();
            try {
                PatternBasedTestCaseImpl tc = instantiateSinglePatternTestFromModel(qef, testURI);
                tests.add(tc);
            } catch (TestCaseInstantiationException e) {
                log.error(e.getMessage(), e);
                if (strict) {
                    throw new TestCaseInstantiationException(e.getMessage(), e);
                }
            }
        }

        return tests;
    }

    /**
     * <p>instantiateSingleManualTestFromModel.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param testURI a {@link java.lang.String} object.
     * @return a {@link ManualTestCaseImpl} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public static ManualTestCaseImpl instantiateSingleManualTestFromModel(QueryExecutionFactory qef, String testURI) throws TestCaseInstantiationException {

        String sparqlSelect = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
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
                String sparqlWhere = qs.get("sparqlWhere").asLiteral().getLexicalForm();
                String sparqlPrevalence = qs.get("sparqlPrevalence").asLiteral().getLexicalForm();
                Collection<String> referencesLst = getReferencesFromTestCase(qef, testURI);
                String testGenerator = "";
                if (qs.contains("testGenerator")) {
                    testGenerator = qs.get("testGenerator").toString();
                }

                // Get annotations from Test URI
                Collection<ResultAnnotation> resultAnnotations = SparqlUtils.getResultAnnotations(qef, testURI);

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
                    ManualTestCaseImpl tc = new ManualTestCaseImpl(
                            testURI,
                            annotation,
                            sparqlWhere,
                            sparqlPrevalence);
                    new TestCaseValidator(tc).validate();
                    return tc;
                }
            }

        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        throw new TestCaseInstantiationException("No results for TC (probably incomplete): " + testURI);
    }

    /**
     * <p>instantiateSinglePatternTestFromModel.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param testURI a {@link java.lang.String} object.
     * @return a {@link PatternBasedTestCaseImpl} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public static PatternBasedTestCaseImpl instantiateSinglePatternTestFromModel(QueryExecutionFactory qef, String testURI) throws TestCaseInstantiationException {

        String sparqlSelect = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
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
                Pattern pattern = PatternService.getPatternFromID(org.aksw.rdfunit.services.PrefixNSService.getLocalName(patternURI, "rutp"));
                if (pattern == null) {
                    throw new TestCaseInstantiationException("Pattern does not exists for TC: " + testURI);
                }

                Collection<String> referencesLst = getReferencesFromTestCase(qef, testURI);
                Collection<Binding> bindings = getBindingsFromTestCase(qef, testURI, pattern);
                String testGenerator = "";
                if (qs.contains("testGenerator")) {
                    testGenerator = qs.get("testGenerator").toString();
                }

                // Get annotations from Test URI
                Collection<ResultAnnotation> resultAnnotations = SparqlUtils.getResultAnnotations(qef, testURI);

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
                    PatternBasedTestCaseImpl tc = new PatternBasedTestCaseImpl(
                            testURI,
                            annotation,
                            pattern,
                            bindings);
                    new TestCaseValidator(tc).validate();
                    return tc;
                }
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        throw new TestCaseInstantiationException("No results for TC (probably incomplete): " + testURI);
    }

    /**
     * <p>writeTestsToFile.</p>
     *
     * @param tests a {@link java.util.Collection} object.
     * @param testCache a {@link org.aksw.rdfunit.io.writer.RDFWriter} object.
     */
    public static void writeTestsToFile(Collection<TestCase> tests, RDFWriter testCache) {
        Model model = ModelFactory.createDefaultModel();
        for (TestCase t : tests) {
            t.serialize(model);
        }
        try {
            org.aksw.rdfunit.services.PrefixNSService.setNSPrefixesInModel(model);
            testCache.write(model);
        } catch (RDFWriterException e) {
            log.error("Cannot cache tests: " + e.getMessage());
        }
    }

    /**
     * <p>getReferencesFromTestCase.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param testURI a {@link java.lang.String} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<String> getReferencesFromTestCase(QueryExecutionFactory qef, String testURI) {

        Collection<String> references = new ArrayList<>();

        String sparqlReferencesSelect = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
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

    /**
     * <p>getBindingsFromTestCase.</p>
     *
     * @param qef a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     * @param testURI a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.elements.interfaces.Pattern} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<Binding> getBindingsFromTestCase(QueryExecutionFactory qef, String testURI, Pattern pattern) {

        Collection<Binding> bindings = new ArrayList<>();

        String sparqlReferencesSelect = org.aksw.rdfunit.services.PrefixNSService.getSparqlPrefixDecl() +
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
                PatternParameter parameter = pattern.getParameter(parameterURI).orNull();
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

    /**
     * <p>generateTestURI.</p>
     *
     * @param sourcePrefix a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.elements.interfaces.Pattern} object.
     * @param bindings a {@link java.util.Collection} object.
     * @param generatorURI a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String generateTestURI(String sourcePrefix, Pattern pattern, Collection<Binding> bindings, String generatorURI) {
        String testURI = org.aksw.rdfunit.services.PrefixNSService.getNSFromPrefix("rutt") + sourcePrefix + "-" + pattern.getId() + "-";
        StringBuilder string2hash = new StringBuilder(generatorURI);
        for (Binding binding : bindings) {
            string2hash.append(binding.getValueAsString());
        }
        String md5Hash = StringUtils.getHashFromString(string2hash.toString());
        if (md5Hash.isEmpty()) {
            return testURI + JenaUUID.generate().asString();
        } else {
            return testURI + md5Hash;
        }
    }


}
