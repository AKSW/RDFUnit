package org.aksw.rdfunit.elements.interfaces;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.ValueKind;

/**
 * Interface for SHACL Arguments
 *
 * @author Dimitris Kontokostas
 * @since 6 /17/15 3:15 PM
 * @version $Id: $Id
 */
public interface Argument extends Element{

    /**
     * Gets the argument comment
     *
     * @return a string as comment for the argument
     */
    String getComment();

    /**
     * Is optional.
     *
     * @return true if the argument is optional, false otherwise
     */
    boolean isOptional();

    /**
     * Gets predicate.
     *
     * @return the predicate
     */
    RDFNode getPredicate();

    /**
     * Gets value type.
     *
     * @return the value type
     */
    Optional<Resource> getValueType();

    /**
     * Gets value kind.
     *
     * @return the value kind
     */
    Optional<ValueKind> getValueKind();

    /**
     * Gets default value.
     *
     * @return the default value
     */
    Optional<RDFNode> getDefaultValue();
}
