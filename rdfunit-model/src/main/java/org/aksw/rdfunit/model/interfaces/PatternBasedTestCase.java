package org.aksw.rdfunit.model.interfaces;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 10/19/15 3:06 PM

 */
public interface PatternBasedTestCase extends TestCase {


    Pattern getPattern();

    Collection<Binding> getBindings();
}
