package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import org.aksw.rdfunit.model.interfaces.TestGenerator;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 2/11/18
 */
public final class TestGeneratorFactory {
    private TestGeneratorFactory(){}

    public static RdfUnitTestGenerator createTagWithCacheGenerator(Collection<TestGenerator> testGenerators, String testFolder) {
        return new FirstSuccessfulTestGenerator(ImmutableList.of(
                new CacheTestGenerator(testFolder),
                new GenerateAndCacheRdfUnitTestGenerator(new TagRdfUnitTestGenerator(testGenerators), testFolder)
        ));
    }

    public static RdfUnitTestGenerator create(Collection<TestGenerator> testGenerators, String testFolder, boolean useAutoTests, boolean loadFromCache, boolean useManualTests) {
        // tag / cache
        ImmutableList.Builder<RdfUnitTestGenerator> builder = ImmutableList.builder();
        if (useAutoTests) {
            if (loadFromCache) {
                builder.add(createTagWithCacheGenerator(testGenerators, testFolder));
            } else {
                builder.add(new TagRdfUnitTestGenerator(testGenerators));
            }
        }
        if (useManualTests) {
            builder.add(new ManualRdfunitTestGenerator(testFolder));
        }
        // add shacl eitherway
        builder.add(new ShaclTestGenerator());

        return new CompositeTestGenerator(builder.build());
    }

    public static RdfUnitTestGenerator createAllWithCache(Collection<TestGenerator> testGenerators, String testFolder) {
        return create(testGenerators, testFolder, true, true, true);
    }

    public static RdfUnitTestGenerator createAllNoCache(Collection<TestGenerator> testGenerators, String testFolder) {
        return create(testGenerators, testFolder, true, false, true);
    }

    public static RdfUnitTestGenerator createShaclOnly() {
        return new ShaclTestGenerator();
    }

    public static RdfUnitTestGenerator createShaclAndManualOnly(String testFolder) {
        return new CompositeTestGenerator(ImmutableList.of(
                new ShaclTestGenerator(),
                new ManualRdfunitTestGenerator(testFolder)
        ));
    }

    public static RdfUnitTestGenerator createShaclAndTagNoManual(Collection<TestGenerator> testGenerators) {
        return new CompositeTestGenerator(ImmutableList.of(
                new ShaclTestGenerator(),
                new TagRdfUnitTestGenerator(testGenerators)
        ));
    }
}
