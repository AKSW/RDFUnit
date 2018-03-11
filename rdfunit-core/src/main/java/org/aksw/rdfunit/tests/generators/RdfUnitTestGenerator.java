package org.aksw.rdfunit.tests.generators;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:50 μμ
 */
public interface RdfUnitTestGenerator {

    Collection<TestCase> generate(SchemaSource source);
    Collection<TestCase> generate(TestSource source);
}
