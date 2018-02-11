package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.sources.SchemaSource;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Instantiates TestCases based on a test generator and a schema
 *
 * @author Dimitris Kontokostas

 * @since 9/26/15 1:23 PM
 */
@Slf4j
public class TestGeneratorTCInstantiator {

    private final ImmutableList<TestGenerator> testGenerators;
    private final SchemaSource source;


    public TestGeneratorTCInstantiator(Collection<TestGenerator> testGenerators, SchemaSource source) {
        this.testGenerators = ImmutableList.copyOf(testGenerators);
        this.source = source;
    }


    public Collection<TestCase> generate() {


        Collection<TestCase> tests = new ArrayList<>();


        for (TestGenerator tg : testGenerators) {
            TagRdfUnitTestGenerator tagRdfUnitTestGenerator =  new TagRdfUnitTestGenerator(tg);
            tests.addAll(tagRdfUnitTestGenerator.generate(source));
        }
        return tests;
    }

}
