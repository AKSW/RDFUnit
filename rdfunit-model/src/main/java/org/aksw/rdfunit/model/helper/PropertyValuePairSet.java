package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * holds a simple set of property value pairs but on build groups the values with the same properties
 *
 * @author Dimitris Kontokostas
 * @since 12/1/2016 11:50 μμ
 */
@Builder
@Value
public class PropertyValuePairSet {
    @Getter
    @Singular
    private final ImmutableSet<PropertyValuePair> annotations;

    private PropertyValuePairSet(ImmutableSet<PropertyValuePair> annotations) {
        this.annotations = ImmutableSet.copyOf(
                groupAnnotationsPerProperty(
                        checkNotNull(annotations)));
    }

    /*
    public Collection<RDFNode> getPropertyValues(Property property) {
        return getPropertyValues(property, this.annotations);
    }*/

    private Collection<RDFNode> getPropertyValues(Property property, Set<PropertyValuePair> annotationSet) {
        return annotationSet.stream()
                .filter(p -> p.getProperty().equals(property))
                .flatMap(p -> p.getValues().stream())
                .collect(Collectors.toList());
    }

    private Set<PropertyValuePair> groupAnnotationsPerProperty(Set<PropertyValuePair> ungroupedAnnotations) {

        return ungroupedAnnotations.stream()
                // get list of properties
                .map(PropertyValuePair::getProperty)
                .distinct() // remove duplicates
                // for every property get all values
                .map(p -> PropertyValuePair.create(p, getPropertyValues(p, ungroupedAnnotations)))
                .collect(Collectors.toSet());
    }
}
