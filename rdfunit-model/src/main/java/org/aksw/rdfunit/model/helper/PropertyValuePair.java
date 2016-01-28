package org.aksw.rdfunit.model.helper;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;

import java.util.Collection;
import java.util.Collections;

/**
 * A simple Property / RDFNode(s) structure that is mainly used in annotations
 *
 * @author Dimitris Kontokostas
 * @version $Id: $Id
 * @since 8/28/15 8:36 PM
 */
@Value
@Builder
public final class PropertyValuePair {

    @Getter
    private final Property property;

    @Singular
    @Getter
    private final Collection<RDFNode> values;

    public static PropertyValuePair create(Property property, RDFNode rdfNode) {
        return create(property, Collections.singletonList(rdfNode));
    }

    public static PropertyValuePair create(Property property, Collection<RDFNode> rdfNode) {
        return PropertyValuePair.builder()
                .property(property)
                .values(rdfNode)
                .build();
    }
}
