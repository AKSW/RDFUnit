package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;

import java.util.Collection;
import java.util.Optional;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 2:30 PM
 * @version $Id: $Id
 */
public interface Template extends Element {

    /**
     * <p>getComponentParameters.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<ComponentParameter> getArguments();

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
