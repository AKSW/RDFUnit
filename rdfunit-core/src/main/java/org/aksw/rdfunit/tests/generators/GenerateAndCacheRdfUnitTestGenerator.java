package org.aksw.rdfunit.tests.generators;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.CacheUtils;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.TestUtils;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */

@Slf4j
public class GenerateAndCacheRdfUnitTestGenerator implements RdfUnitTestGenerator{

    private final TagRdfUnitTestGenerator tagRdfUnitTestGenerator;
    private final String testFolder;


    public GenerateAndCacheRdfUnitTestGenerator(TagRdfUnitTestGenerator tagRdfUnitTestGenerator, String testFolder) {
        this.tagRdfUnitTestGenerator = tagRdfUnitTestGenerator;
        this.testFolder = testFolder;
    }


    @Override
    public Collection<TestCase> generate(TestSource source) {
        Collection<TestCase> tests = tagRdfUnitTestGenerator.generate(source);
        if (!tests.isEmpty()) {
            TestUtils.writeTestsToFile(tests, new RdfFileWriter(CacheUtils.getSourceAutoTestFile(testFolder, source)));

        }
        return tests;
    }

    @Override
    public Collection<TestCase> generate(SchemaSource source) {
        Collection<TestCase> tests = tagRdfUnitTestGenerator.generate(source);
        if (!tests.isEmpty()) {
            TestUtils.writeTestsToFile(tests, new RdfFileWriter(CacheUtils.getSourceAutoTestFile(testFolder, source)));

        }
        return tests;
    }

}
