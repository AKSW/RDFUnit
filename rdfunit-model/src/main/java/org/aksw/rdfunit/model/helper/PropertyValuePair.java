package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 8:36 PM
 * @version $Id: $Id
 */
public final class PropertyValuePair {
    private final Property property;
    private final Collection<RDFNode> values;

    private PropertyValuePair(Property property, Collection<RDFNode> values) {
        this.property = checkNotNull(property);
        this.values = ImmutableList.copyOf(checkNotNull(values));
    }

    /**
     * <p>create.</p>
     *
     * @param property a {@link com.hp.hpl.jena.rdf.model.Property} object.
     * @param rdfNode a {@link com.hp.hpl.jena.rdf.model.RDFNode} object.
     * @return a {@link PropertyValuePair} object.
     */
    public static PropertyValuePair create(Property property, RDFNode rdfNode) {
        return new PropertyValuePair(property, Collections.singletonList(rdfNode));
    }

    /**
     * <p>create.</p>
     *
     * @param property a {@link com.hp.hpl.jena.rdf.model.Property} object.
     * @param rdfNode a {@link java.util.Collection} object.
     * @return a {@link PropertyValuePair} object.
     */
    public static PropertyValuePair create(Property property, Collection<RDFNode> rdfNode) {
        return new PropertyValuePair(property, rdfNode);
    }

    /**
     * <p>Getter for the field <code>property</code>.</p>
     *
     * @return a {@link com.hp.hpl.jena.rdf.model.Property} object.
     */
    public Property getProperty() {
        return property;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<RDFNode> getValues() {
        return values;
    }
}
