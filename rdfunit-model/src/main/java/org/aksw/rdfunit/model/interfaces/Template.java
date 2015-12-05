package org.aksw.rdfunit.model.interfaces;

import com.google.common.base.Optional;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 2:30 PM
 * @version $Id: $Id
 */
public interface Template extends Element {

    /**
     * <p>getArguments.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<Argument> getArguments();

    /**
     * <p>getSparql.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getSparql();

    /**
     * <p>getLabelTemplate.</p>
     *
     * @return a {@link com.google.common.base.Optional} object.
     */
    Optional<String> getLabelTemplate();
}
