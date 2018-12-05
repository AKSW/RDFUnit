package org.aksw.rdfunit.validate.wrappers;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RdfFirstSuccessReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.sources.TestSourceFactory;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/7/15 10:13 AM

 */
@Slf4j
public final class RDFUnitTestSuiteGenerator {

    @Getter(lazy = true) @NonNull private final RDFUnit rdfUnit = initRdfUnit();
    @Getter(lazy = true) @NonNull private final TestSuite testSuite = initTestSuite();

    private final Collection<SchemaSource> schemas;

    private final boolean enableAutoTests;
    private final boolean enableManualTests;
    private final boolean enableLoadFromCache = false;


    private RDFUnitTestSuiteGenerator(Collection<SchemaSource> schemas, boolean enableAutoTests, boolean enableManualTests) {
        this.enableAutoTests = enableAutoTests;
        this.enableManualTests = enableManualTests;
        this.schemas = Collections.unmodifiableCollection(checkNotNull(schemas));
    }

    private RDFUnit initRdfUnit() {
        return RDFUnit.createWithOwlAndShacl().init();
    }

    private TestSuite initTestSuite() {

        // Generate test cases from ontology (do this every time in case ontology changes)

        TestSource dummyTestSource = TestSourceFactory.createDumpTestSource("dummy", "dummy", RdfReaderFactory.createEmptyReader(), schemas);

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(
                enableAutoTests,
                enableLoadFromCache, // set to false
                enableManualTests);
        return  testGeneratorExecutor.generateTestSuite("", dummyTestSource, getRdfUnit().getAutoGenerators());
    }

    public Collection<SchemaSource> getSchemas() {
        return schemas;
    }


    public static class Builder {
        private Collection<SchemaSource> schemas = new ArrayList<>();

        private boolean enableAutoTests = true;
        private boolean enableManualTests = true;

        public Builder enableAutotests() {
            enableAutoTests = true;
            return this;
        }

        public Builder disableAutotests() {
            enableAutoTests = false;
            return this;
        }
        public Builder enableManualtests() {
            enableManualTests = true;
            return this;
        }
        public Builder disableManualtests() {
            enableManualTests = false;
            return this;
        }

        public Builder addSchemaURI(String prefix, String schemaUri) {
            checkNotNull(schemaUri);

            SchemaSource schemaSource = createSource(prefix, schemaUri, RdfReaderFactory.createDereferenceReader(schemaUri));
            schemas.add(schemaSource);

            return this;
        }

        public Builder addLocalResource(String prefix, String localResource) {
            checkNotNull(localResource);

            SchemaSource schemaSource = createSource(prefix, localResource, RdfReaderFactory.createResourceReader(localResource));
            schemas.add(schemaSource);

            return this;
        }

        public Builder addLocalResourceOrSchemaURI(String prefix, String localResource, String schemaUri) {
            checkNotNull(localResource);
            checkNotNull(schemaUri);

            RdfReader schemaReader = new RdfFirstSuccessReader(
                    Arrays.asList(
                            RdfReaderFactory.createResourceReader(localResource),
                            RdfReaderFactory.createDereferenceReader(schemaUri)
                    ));

            SchemaSource schemaSource = createSource(prefix, schemaUri, schemaReader);
            schemas.add(schemaSource);

            return this;
        }

        public Builder addSchemaURI(String prefix, String schemaUri, RdfReader rdfReader) {
            schemas.add(createSource(prefix, checkNotNull(schemaUri), checkNotNull(rdfReader)));
            return this;
        }

        public RDFUnitTestSuiteGenerator build() {
            return new RDFUnitTestSuiteGenerator(schemas, enableAutoTests, enableManualTests);
        }

        private SchemaSource createSource(String prefix, String schemaUri, RdfReader schemaReader) {
            return SchemaSourceFactory.createSchemaSourceSimple(prefix, schemaUri, schemaReader);
        }
    }
}
