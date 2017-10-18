package org.aksw.rdfunit;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.NonNull;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.io.reader.*;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.model.readers.BatchPatternReader;
import org.aksw.rdfunit.model.readers.BatchTestGeneratorReader;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

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

    @Getter(lazy = true) @NonNull private final ImmutableSet<TestGenerator> autoGenerators = generateAutoGenerators();
    @Getter(lazy = true) @NonNull private final ImmutableSet<Pattern> patterns = generatePatterns();
    @Getter(lazy = true) @NonNull private final QueryExecutionFactoryModel patternQueryFactory = generateExecutionFactory();

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
        this(Collections.singletonList(baseDirectory));
    }

    /**
     * <p>Constructor for RDFUnit.</p>
     */
    public RDFUnit() {
        this(new ArrayList<>());
    }

    private QueryExecutionFactoryModel generateExecutionFactory(){
        Model model = ModelFactory.createDefaultModel();
        // Set the defined prefixes
        PrefixNSService.setNSPrefixesInModel(model);

        try {
            getPatternsReader(baseDirectories).read(model);
            getAutoGeneratorsALLReader(baseDirectories).read(model);
        } catch (RdfReaderException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return new QueryExecutionFactoryModel(model);

        // Update pattern service

    }


    /**
     * Initializes the patterns library, required
     *
     * @throws IllegalArgumentException if files do not exist
     */
    public void init() {

        // Update pattern service
        for (Pattern pattern : getPatterns()) {
            PatternService.addPattern(pattern.getId(),pattern.getIRI(), pattern);
        }
    }

    private ImmutableSet<TestGenerator> generateAutoGenerators() {
         return ImmutableSet.copyOf(
            BatchTestGeneratorReader.create().getTestGeneratorsFromModel(getPatternQueryFactory().getModel()));
    }

    private ImmutableSet<Pattern> generatePatterns() {
        return ImmutableSet.copyOf(
                BatchPatternReader.create().getPatternsFromModel(getPatternQueryFactory().getModel()));
    }

    private static RdfReader createReaderFromBaseDirsAndResource(Collection<String> baseDirectories, String relativeName) {
        ArrayList<RdfReader> readers = new ArrayList<>();
        for (String baseDirectory : baseDirectories) {
            String normalizedBaseDir = baseDirectory.endsWith("/") ? baseDirectory : baseDirectory + "/";
            readers.add(new RdfStreamReader(normalizedBaseDir + relativeName));
        }
        readers.add(RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/configuration/" + relativeName));
        return new RdfFirstSuccessReader(readers);
    }

    /**
     * <p>getPatternsReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getPatternsReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "patterns.ttl");
    }

    /**
     * <p>getAutoGeneratorsOWLReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsOWLReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsOWL.ttl");
    }

    /**
     * <p>getAutoGeneratorsOWLReader.</p>
     *
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsOWLReader() {
        return getAutoGeneratorsOWLReader(new ArrayList<>());
    }

    /**
     * <p>getAutoGeneratorsDSPReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsDSPReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsDSP.ttl");
    }

    /**
     * <p>getAutoGeneratorsDSPReader.</p>
     *
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsDSPReader() {
        return getAutoGeneratorsDSPReader(new ArrayList<>());
    }

    /**
     * <p>getAutoGeneratorsRSReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsRSReader(Collection<String> baseDirectories) {
        return createReaderFromBaseDirsAndResource(baseDirectories, "autoGeneratorsRS.ttl");
    }

    /**
     * <p>getAutoGeneratorsRSReader.</p>
     *
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsRSReader() {
        return getAutoGeneratorsRSReader(new ArrayList<>());
    }

    /**
     * <p>getAutoGeneratorsALLReader.</p>
     *
     * @param baseDirectories a {@link java.util.Collection} object.
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsALLReader(Collection<String> baseDirectories) {
        Collection<RdfReader> readers = Arrays.asList(
                getAutoGeneratorsOWLReader(baseDirectories),
                getAutoGeneratorsDSPReader(baseDirectories),
                getAutoGeneratorsRSReader(baseDirectories)
        );

        return new RdfMultipleReader(readers);
    }

    /**
     * <p>getAutoGeneratorsALLReader.</p>
     *
     * @return a {@link RdfReader} object.
     */
    public static RdfReader getAutoGeneratorsALLReader() {
        return getAutoGeneratorsALLReader(new ArrayList<>());
    }
}
