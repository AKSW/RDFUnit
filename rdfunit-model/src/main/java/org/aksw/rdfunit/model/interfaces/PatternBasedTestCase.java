package org.aksw.rdfunit.model.interfaces;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 3:06 PM
 * @version $Id: $Id
 */
public interface PatternBasedTestCase extends TestCase {


    /**
     * <p>getPattern.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     */
    Pattern getPattern();
    /**
     * <p>getBindings.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<Binding> getBindings();
}
