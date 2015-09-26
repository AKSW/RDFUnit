package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 8:36 PM
 */
public final class SimpleAnnotation {
    private final Property property;
    private final Collection<RDFNode> values;

    private SimpleAnnotation(Property property, Collection<RDFNode> values) {
        this.property = checkNotNull(property);
        this.values = ImmutableList.copyOf(checkNotNull(values));
    }

    public static SimpleAnnotation create(Property property, RDFNode rdfNode) {
        return new SimpleAnnotation(property, Arrays.asList(rdfNode));
    }

    public static SimpleAnnotation create(Property property, Collection<RDFNode> rdfNode) {
        return new SimpleAnnotation(property, rdfNode);
    }

    public Property getProperty() {
        return property;
    }

    public Collection<RDFNode> getValues() {
        return values;
    }
}
