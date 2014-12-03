package org.aksw.rdfunit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.PatternUtils;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.io.reader.*;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestAutoGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Main class used to load and instantiate patterns and provide access to Test Generators
 *
 * @author Dimitris Kontokostas
 * @since 9/20/13 5:59 PM
 */
public class RDFUnit {

    private final Collection<String> baseDirectories;

    private volatile Collection<TestAutoGenerator> autoGenerators;
    private volatile Collection<Pattern> patterns;
    private volatile QueryExecutionFactory patternQueryFactory;

    public RDFUnit(Collection<String> baseDirectories) {
        this.baseDirectories = baseDirectories;
    }

    public RDFUnit(String baseDirectory) {
        this(Arrays.asList(baseDirectory));
    }

    public RDFUnit() {
        this(new ArrayList<String>());
    }

    public void init() throws RDFReaderException {
        Model model = ModelFactory.createDefaultModel();
        // Set the defined prefixes
        PrefixNSService.setNSPrefixesInModel(model);

        try {
            getPatternsReader(baseDirectories).read(model);
            getAutoGeneratorsALLReader(baseDirectories).read(model);
        } catch (RDFReaderException e) {
            throw new RDFReaderException(e.getMessage(), e);
        }

        patternQueryFactory = new QueryExecutionFactoryModel(model);

        // Update pattern service
        for (Pattern pattern : getPatterns()) {
            PatternService.addPattern(pattern.getId(), pattern);
        }
    }

    private synchronized Collection<Pattern> getPatterns() {
        if (patterns == null) {
            patterns =
                    Collections.unmodifiableCollection(
                            PatternUtils.instantiatePatternsFromModel(patternQueryFactory));
        }
        return patterns;
    }

    public synchronized Collection<TestAutoGenerator> getAutoGenerators() {
        if (autoGenerators == null) {
            autoGenerators =
                    Collections.unmodifiableCollection(
                            TestUtils.instantiateTestGeneratorsFromModel(patternQueryFactory));
        }
        return autoGenerators;
    }

    private static RDFReader createReaderFromBaseDirsAndResource(Collection<String> baseDirectories, String relativeName) {
        ArrayList<RDFReader> readers = new ArrayList<>();
        for (String baseDirectory : baseDirectories) {
            String normalizedBaseDir = (baseDirectory.endsWith("/") ? baseDirectory : baseDirectory + "/");
            readers.add(new RDFStreamReader(normalizedBaseDir + relativeName));
        }
        readers.add(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/" + relativeName));
        return new RDFFirstSuccessReader(readers);
    }

    public static RDFReader getPatternsReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "patterns.ttl");
    }

    public static RDFReader getPatternsReader() {
        return getPatternsReader(new ArrayList<String>());
    }

    public static RDFReader getAutoGeneratorsOWLReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsOWL.ttl");
    }

    public static RDFReader getAutoGeneratorsOWLReader() {
        return getAutoGeneratorsOWLReader(new ArrayList<String>());
    }

    public static RDFReader getAutoGeneratorsDSPReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsDSP.ttl");
    }

    public static RDFReader getAutoGeneratorsDSPReader() {
        return getAutoGeneratorsDSPReader(new ArrayList<String>());
    }

    public static RDFReader getAutoGeneratorsRSReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsRS.ttl");
    }

    public static RDFReader getAutoGeneratorsRSReader() {
        return getAutoGeneratorsRSReader(new ArrayList<String>());
    }

    public static RDFReader getAutoGeneratorsALLReader(Collection<String> baseDirectories) {
        Collection<RDFReader> readers = Arrays.asList(
                getAutoGeneratorsOWLReader(baseDirectories),
                getAutoGeneratorsDSPReader(baseDirectories),
                getAutoGeneratorsRSReader(baseDirectories)
        );

        return new RDFMultipleReader(readers);
    }

    public static RDFReader getAutoGeneratorsALLReader() {
        return getAutoGeneratorsALLReader(new ArrayList<String>());
    }
}
