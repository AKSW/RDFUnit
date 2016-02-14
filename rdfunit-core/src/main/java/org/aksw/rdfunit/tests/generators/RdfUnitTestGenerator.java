package org.aksw.rdfunit.tests.generators;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;

import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:50 μμ
 */
public interface RdfUnitTestGenerator {

    Set<TestCase> generate(SchemaSource source);
}
