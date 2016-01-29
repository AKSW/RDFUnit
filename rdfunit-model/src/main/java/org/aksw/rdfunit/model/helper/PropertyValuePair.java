package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple Property / RDFNode(s) structure that is mainly used in annotations
 *
 * @author Dimitris Kontokostas
 * @version $Id: $Id
 * @since 8/28/15 8:36 PM
 */
@Value
public final class PropertyValuePair {

    @Getter
    private final Property property;

    @Singular
    @Getter
    private final ImmutableSet<RDFNode> values;

    private PropertyValuePair(Property property, Collection<RDFNode> values) {
        this.property = checkNotNull(property);
        this.values = ImmutableSet.copyOf(checkNotNull(values));
    }

    public static PropertyValuePair create(Property property, RDFNode rdfNode) {
        return create(property, Collections.singletonList(rdfNode));
    }

    public static PropertyValuePair create(Property property, Collection<RDFNode> rdfNode) {
        return new PropertyValuePair(property, rdfNode);
    }
}
