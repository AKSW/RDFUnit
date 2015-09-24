package org.aksw.rdfunit.model.interfaces;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Describes an annotation on a Shape or RDFUnit tests, patterns and generators
 *
 * @author Dimitris Kontokostas
 * @since 8 /15/15 4:26 PM
 * @version $Id: $Id
 */
public interface ResultAnnotation extends Element{
    /**
     * Gets the annotation property.
     *
     * @return the annotation property
     */
    Property getAnnotationProperty();

    /**
     * Gets the annotation value (if any).
     *
     * @return the annotation value
     */
    Optional<RDFNode> getAnnotationValue();

    /**
     * Gets the annotation variable name (if any)
     *
     * @return the annotation var name
     */
    Optional<String> getAnnotationVarName();
}
