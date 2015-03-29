package org.aksw.rdfunit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.PatternUtils;
import org.aksw.rdfunit.Utils.TestGeneratorUtils;
import org.aksw.rdfunit.io.reader.*;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.tests.TestAutoGenerator;
import org.aksw.rdfunit.utils.PrefixNSService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Main class used to load and instantiate patterns and provide access to Test Generators
 *
 * @author Dimitris Kontokostas
 * @since 9/20/13 5:59 PM
 * @version $Id: $Id
 */
public class RDFUnit {

    private final Collection<String> baseDirectories;

    private volatile Collection<TestAutoGenerator> autoGenerators;
    private volatile Collection<Pattern> patterns;
    private volatile QueryExecutionFactory patternQueryFactory;

    /**
     * <p>Constructor for RDFUnit.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     */
    public RDFUnit(Collection<String> baseDirectories) {
        this.baseDirectories = baseDirectories;
    }

    /**
     * <p>Constructor for RDFUnit.</p>
     *
     * @param baseDirectory a {@link java.lang.String} object.
     */
    public RDFUnit(String baseDirectory) {
        this(Arrays.asList(baseDirectory));
    }

    /**
     * <p>Constructor for RDFUnit.</p>
     */
    public RDFUnit() {
        this(new ArrayList<String>());
    }

    /**
     * <p>init.</p>
     *
     * @throws org.aksw.rdfunit.io.reader.RDFReaderException if any.
     */
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

    /**
     * <p>Getter for the field <code>autoGenerators</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public synchronized Collection<TestAutoGenerator> getAutoGenerators() {
        if (autoGenerators == null) {
            autoGenerators =
                    Collections.unmodifiableCollection(
                            TestGeneratorUtils.instantiateTestGeneratorsFromModel(patternQueryFactory));
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

    /**
     * <p>getPatternsReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getPatternsReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "patterns.ttl");
    }

    /**
     * <p>getPatternsReader.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getPatternsReader() {
        return getPatternsReader(new ArrayList<String>());
    }

    /**
     * <p>getAutoGeneratorsOWLReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsOWLReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsOWL.ttl");
    }

    /**
     * <p>getAutoGeneratorsOWLReader.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsOWLReader() {
        return getAutoGeneratorsOWLReader(new ArrayList<String>());
    }

    /**
     * <p>getAutoGeneratorsDSPReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsDSPReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsDSP.ttl");
    }

    /**
     * <p>getAutoGeneratorsDSPReader.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsDSPReader() {
        return getAutoGeneratorsDSPReader(new ArrayList<String>());
    }

    /**
     * <p>getAutoGeneratorsRSReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsRSReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsRS.ttl");
    }

    /**
     * <p>getAutoGeneratorsRSReader.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsRSReader() {
        return getAutoGeneratorsRSReader(new ArrayList<String>());
    }

    /**
     * <p>getAutoGeneratorsALLReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsALLReader(Collection<String> baseDirectories) {
        Collection<RDFReader> readers = Arrays.asList(
                getAutoGeneratorsOWLReader(baseDirectories),
                getAutoGeneratorsDSPReader(baseDirectories),
                getAutoGeneratorsRSReader(baseDirectories)
        );

        return new RDFMultipleReader(readers);
    }

    /**
     * <p>getAutoGeneratorsALLReader.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader getAutoGeneratorsALLReader() {
        return getAutoGeneratorsALLReader(new ArrayList<String>());
    }
}
