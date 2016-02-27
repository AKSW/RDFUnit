package org.aksw.rdfunit.utils;

import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.io.writer.RdfWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.readers.BatchTestCaseReader;
import org.aksw.rdfunit.model.writers.TestCaseWriter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    /**
     * <p>instantiateTestsFromModel.</p>
     *
     * @param model a {@link org.apache.jena.rdf.model.Model} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<TestCase> instantiateTestsFromModel(Model model) {
        try {
            return instantiateTestsFromModel(model, false);
        } catch (TestCaseInstantiationException e) {
            LOGGER.warn("TestCase instantiation failed", e);
        }
        throw new IllegalArgumentException("Unexpected exception...");
    }

    /**
     * <p>instantiateTestsFromModel.</p>
     *
     * @param model a {@link org.apache.jena.rdf.model.Model} object.
     * @param strict a boolean.
     * @return a {@link java.util.Collection} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public static Collection<TestCase> instantiateTestsFromModel(Model model, boolean strict) throws TestCaseInstantiationException {
        return BatchTestCaseReader.create().getTestCasesFromModel(model);
    }


    /**
     * <p>writeTestsToFile.</p>
     *
     * @param tests a {@link java.util.Collection} object.
     * @param testCache a {@link RdfWriter} object.
     */
    public static void writeTestsToFile(Collection<TestCase> tests, RdfWriter testCache) {
        Model model = ModelFactory.createDefaultModel();
        for (TestCase t : tests) {
            TestCaseWriter.create(t).write(model);
        }
        try {
            org.aksw.rdfunit.services.PrefixNSService.setNSPrefixesInModel(model);
            testCache.write(model);
        } catch (RdfWriterException e) {
            LOGGER.error("Cannot cache tests", e);
        }
    }

    /**
     * <p>generateTestURI.</p>
     *
     * @param sourcePrefix a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
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
            return JenaUtils.getUniqueIri(testURI);
        } else {
            return testURI + md5Hash;
        }
    }


}
