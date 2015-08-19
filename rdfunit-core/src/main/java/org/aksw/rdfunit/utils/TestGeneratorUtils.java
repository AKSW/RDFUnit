package org.aksw.rdfunit.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.elements.interfaces.TestGenerator;
import org.aksw.rdfunit.elements.readers.TestGeneratorReader;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
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
    public static Collection<TestGenerator> instantiateTestGeneratorsFromModel(Model model) {
        Collection<TestGenerator> autoGenerators = new ArrayList<>();

        for (Resource r: model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList() ) {
            TestGenerator tag = TestGeneratorReader.create().read(r);
            if (tag.isValid()) {
                autoGenerators.add(tag);
            } else {
                log.error("AutoGenerator not valid: " + tag.getTAGUri());
                System.exit(-1);
            }
        }

        return autoGenerators;
    }

    /**
     * <p>instantiateTestsFromAG.</p>
     *
     * @param autoGenerators a {@link java.util.Collection} object.
     * @param source a {@link org.aksw.rdfunit.sources.Source} object.
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<TestCase> instantiateTestsFromAG(Collection<TestGenerator> autoGenerators, Source source) {
    public static Collection<TestCase> instantiateTestsFromAG(Collection<TestAutoGenerator> autoGenerators, SchemaSource source) {
        Collection<TestCase> tests = new ArrayList<>();

        for (TestGenerator tag : autoGenerators) {
            tests.addAll(tag.generate(source));
        }

        return tests;

    }
}
