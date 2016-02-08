package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.apache.jena.rdf.model.Property;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 2:12 PM
 * @version $Id: $Id
 */
public interface ConstraintTemplate extends Template {

    //TODO multiple languages
    /**
     * <p>getDefaultMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getDefaultMessage();

    /**
     * <p>getProperty.</p>
     *
     * @return a {@link org.apache.jena.rdf.model.Property} object.
     */
    Property getProperty();

    /**
     * <p>getResultAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<ResultAnnotation> getResultAnnotations();

    /**
     * <p>getSeverity.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     */
    RLOGLevel getSeverity();
}
