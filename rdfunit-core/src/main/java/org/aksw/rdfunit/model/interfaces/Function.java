package org.aksw.rdfunit.model.interfaces;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.List;

/**
 * Interface to a function according to SHACL
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 3:10 PM
 * @version $Id: $Id
 */
public interface Function extends Element {

    /**
     * <p>getComment.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getComment();
    /**
     * <p>getReturnType.</p>
     *
     * @return a {@link com.google.common.base.Optional} object.
     */
    Optional<Resource> getReturnType();
    /**
     * <p>isCachable.</p>
     *
     * @return a boolean.
     */
    boolean isCachable();

    /**
     * <p>getArguments.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Argument> getArguments();
    /**
     * <p>getSparqlString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getSparqlString();

}
