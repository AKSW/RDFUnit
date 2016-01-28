package org.aksw.rdfunit.model.interfaces;

import java.util.Collection;

/**
 * TestGenerator Interface
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 3:10 PM
 * @version $Id: $Id
 */
public interface TestGenerator extends Element {

    String getUri();

    String getDescription();

    String getQuery();

    Pattern getPattern();

    Collection<ResultAnnotation> getAnnotations();

    //Collection<TestCase> generate(SchemaSource source);

    /**
     * Checks if the the generator is valid (provides correct parameters)
     */
    boolean isValid();

}
