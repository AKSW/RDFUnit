package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.PatternParameterConstraints;

import java.util.Optional;

/**
 * Defines a Pattern Parameter.
 *
 * @author Dimitris Kontokostas
 * @since 9 /20/13 2:47 PM
 * @version $Id: $Id
 */
public interface PatternParameter extends Element{


    /**
     * Gets uri.
     *
     * @return the uri
     */
    String getUri();

    /**
     * Gets id.
     *
     * @return the id
     */
    String getId();

    /**
     * Gets constrain.
     *
     * @return the constrain
     */
    PatternParameterConstraints getConstraint();

    /**
     * Gets constraint pattern.
     *
     * @return the constraint pattern
     */
    Optional<String> getConstraintPattern();


}
