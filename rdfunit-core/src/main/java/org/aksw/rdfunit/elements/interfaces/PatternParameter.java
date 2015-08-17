package org.aksw.rdfunit.elements.interfaces;

import com.google.common.base.Optional;
import org.aksw.rdfunit.enums.PatternParameterConstraints;

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
