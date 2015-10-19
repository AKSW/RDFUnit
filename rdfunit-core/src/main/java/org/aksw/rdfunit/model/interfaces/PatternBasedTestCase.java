package org.aksw.rdfunit.model.interfaces;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 3:06 PM
 */
public interface PatternBasedTestCase extends TestCase {


    public Pattern getPattern();
    public Collection<Binding> getBindings();
}
