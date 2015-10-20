package org.aksw.rdfunit.model.interfaces;

/**
 *  aSHACL scope
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 11:46 AM
 * @version $Id: $Id
 */
public interface ShapeScope extends Element{

    //TODO refactor this later when we support more scope types
    /**
     * <p>getScopeClass.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getScopeClass();

}
