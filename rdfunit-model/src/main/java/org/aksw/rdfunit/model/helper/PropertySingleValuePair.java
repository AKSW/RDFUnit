package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Value;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * SImilar to propertyValuePair but here, each object holds a singel value
 *
 * @author Dimitris Kontokostas
 * @version $Id: $Id
 * @since 8/28/15 8:36 PM
 */
@Value
public final class PropertySingleValuePair {

    @Getter
    private final Property property;

    @Getter
    private final RDFNode value;

    private PropertySingleValuePair(Property property, RDFNode value) {
        this.property = checkNotNull(property);
        this.value = checkNotNull(value);
    }

    public static PropertySingleValuePair create(Property property, RDFNode rdfNode) {
        return new PropertySingleValuePair(property, rdfNode);
    }

    public static Set<PropertySingleValuePair> create(Property property, Collection<RDFNode> rdfNodes) {
        ImmutableSet.Builder<PropertySingleValuePair> pairs = ImmutableSet.builder();
        rdfNodes.forEach(n -> pairs.add(create(property, n)));
        return pairs.build();
    }


    public static Set<PropertySingleValuePair> create(PropertyValuePair pair) {
        return create(pair.getProperty(), pair.getValues());
    }
}
