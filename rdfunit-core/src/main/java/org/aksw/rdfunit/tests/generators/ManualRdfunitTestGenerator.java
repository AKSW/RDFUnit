package org.aksw.rdfunit.tests.generators;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.CacheUtils;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.TestUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */
@Slf4j
public final class ManualRdfunitTestGenerator implements RdfUnitTestGenerator{

    private final String testFolder;

    public ManualRdfunitTestGenerator(String testFolder) {
        this.testFolder = testFolder;
    }

    @Override
    public Collection<TestCase> generate(SchemaSource source) {
        return generateFromSource(source, source.getModel());
    }

    @Override
    public Collection<TestCase> generate(TestSource source) {
        return generateFromSource(source, ModelFactory.createDefaultModel());
    }

    private Collection<TestCase> generateFromSource(Source source, Model sourceModel) {
            Set<TestCase> tests = new HashSet<>();

            try {
                RdfReader reader = RdfReaderFactory.createFileOrResourceReader(
                        CacheUtils.getSourceManualTestFile(testFolder, source),                 // check for local directory first
                        CacheUtils.getSourceManualTestFile("/org/aksw/rdfunit/tests/", source)  // otherwise check if it exists in resources
                );
                Collection<TestCase> testsManualsExternal = TestUtils.instantiateTestsFromModel(reader.read());
                tests.addAll(testsManualsExternal);
                tests.addAll(TestUtils.instantiateTestsFromModel(sourceModel));

            } catch (RdfReaderException e) {
                // Do nothing, Manual tests do not exist
                log.debug("No manual tests found for {}", source.getUri());

            }
        log.info("{} identified {} manual tests", source.getUri(), tests.size());
        return tests;

    }
}
