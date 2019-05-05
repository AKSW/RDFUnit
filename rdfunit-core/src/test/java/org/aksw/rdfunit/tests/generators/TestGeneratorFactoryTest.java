package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.assertj.core.util.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dimitris Kontokostas
 * @since 2/14/18
 */
public class TestGeneratorFactoryTest {

    private static final TestSource testSource = TestSourceFactory.createDumpTestSource("dbpedia.org", "http://dbpedia.org", ModelFactory.createDefaultModel(), ImmutableList.of());
    private static final SchemaSource schemaSourceOwl = SchemaSourceFactory
            .createSchemaSourceSimple("exowl", "http://example.com/owl",
                    RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/tests/generators/generate.test.owl.ttl"));
    private static final SchemaSource schemaSourceShacl = SchemaSourceFactory
            .createSchemaSourceSimple("exshacl", "http://example.com/shacl",
                    RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/tests/generators/generate.test.shacl.ttl"));

    private static final String testFolder = "/tmp/";

    private static RDFUnit rdfUnit;
    @Before
    public void setUp() {
        rdfUnit = RDFUnit
            .createWithAllGenerators()
            .init();
    }

    @Test
    public void testManualTests() {

        assertThat(new ManualRdfunitTestLoaderGenerator(testFolder).generate(schemaSourceOwl))
                .isEmpty();

        // dataset has manual tests from manual-tests module
        assertThat(new ManualRdfunitTestLoaderGenerator(testFolder).generate(testSource))
                .isNotEmpty();
    }

    @Test
    public void testTagAndCache() {
        File directory = Files.newTemporaryFolder();
        directory.deleteOnExit();

        RdfUnitTestGenerator g = new GenerateAndCacheRdfUnitTestGenerator(
                new TagRdfUnitTestGenerator(rdfUnit.getAutoGenerators()),
                directory.getAbsolutePath());

        assertThat(directory.listFiles().length == 0).isTrue();
        assertThat(g.generate(schemaSourceOwl))
                .isNotEmpty();
        assertThat(directory.listFiles().length == 1).isTrue();

        // read the cached data
        assertThat(new CacheTestGenerator(directory.getAbsolutePath())
        .generate(schemaSourceOwl))
                .isNotEmpty();
    }

    @Test
    public void testShacl() {
        RdfUnitTestGenerator g = new ShaclTestGenerator();

        assertThat(g.generate(schemaSourceShacl))
                .isNotEmpty();
        assertThat(g.generate(schemaSourceOwl))
                .isEmpty();
    }


    @Test
    public void testAllNoCache() {
        RdfUnitTestGenerator g = TestGeneratorFactory.createAllNoCache(rdfUnit.getAutoGenerators(), testFolder);

        assertThat(g.generate(schemaSourceShacl))
                .isNotEmpty();
        assertThat(g.generate(schemaSourceOwl))
                .isNotEmpty();
        assertThat(g.generate(testSource))
                .isNotEmpty();

    }

}