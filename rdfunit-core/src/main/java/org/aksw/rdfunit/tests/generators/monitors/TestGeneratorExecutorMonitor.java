package org.aksw.rdfunit.tests.generators.monitors;

import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.sources.Source;

/**
 * Interface for monitoring a Test Generator Executor.
 *
 * @author Dimitris Kontokostas
 * @since 1 /3/14 12:23 PM
 * @version $Id: $Id
 */
public interface TestGeneratorExecutorMonitor {
    /**
     * Called when testing starts
     *
     * @param source          the source
     * @param numberOfSources the number of sources
     */
    void generationStarted(final Source source, final long numberOfSources);

    /**
     * Called when a test generation starts
     *
     * @param source         the source
     * @param generationType the generation type
     */
    void sourceGenerationStarted(final Source source, final TestGenerationType generationType);

    /**
     * Called when a test generation starts
     *
     * @param source         the source
     * @param generationType the generation type
     * @param testsCreated   the tests created
     */
    void sourceGenerationExecuted(final Source source, final TestGenerationType generationType, final long testsCreated);

    /**
     * Called when test generation ends
     */
    void generationFinished();
}
