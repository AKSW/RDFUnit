package org.aksw.rdfunit.tests.generators.monitors;

import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.sources.Source;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 12:23 PM
 */
public interface TestGeneratorExecutorMonitor {
    /*
    * Called when testing starts
    * */
    void generationStarted(final Source source, final long numberOfSources);

    /*
    * Called when a test generation starts
    * */
    void sourceGenerationStarted(final Source source, final TestGenerationType generationType);

    /*
    * Called when a test generation starts
    * */
    void sourceGenerationExecuted(final Source source, final TestGenerationType generationType, final long testsCreated);

    /*
    * Called when test generation ends
    * */
    void generationFinished();
}
