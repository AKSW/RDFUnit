package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfStreamReader;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.sources.CacheUtils;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.utils.TestUtils;
import org.apache.jena.rdf.model.Model;

import java.io.File;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */
@Slf4j
public class CacheTestGenerator implements RdfUnitTestGenerator{


    private final String testFolder;

    public CacheTestGenerator(String testFolder) {
        this.testFolder = testFolder;
    }

    @Override
    public Collection<? extends GenericTestCase> generate(SchemaSource source) {
        String cachedTestsLocation = CacheUtils.getSourceAutoTestFile(testFolder, source);
        File f = new File(cachedTestsLocation);
        if (!f.exists())
            return ImmutableList.of();

        try {
            Model m = new RdfStreamReader(cachedTestsLocation).read();
            Collection<GenericTestCase> tests = TestUtils.instantiateTestsFromModel(m);
            log.info("{} contains {} automatically created tests (loaded from cache)", source.getUri(), tests.size());
            return tests;

        } catch (RdfReaderException e) {
            log.error("Cannot read cache test file {}", cachedTestsLocation, e);
            throw new IllegalArgumentException("Cannot read cache test file "+ cachedTestsLocation, e);
        }

    }

    @Override
    public Collection<? extends GenericTestCase> generate(TestSource source) {
        return ImmutableList.of();
    }
}
