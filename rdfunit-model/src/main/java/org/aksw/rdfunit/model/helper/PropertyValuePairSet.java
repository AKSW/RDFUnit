package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Dimitris Kontokostas
 * @since 12/1/2016 11:50 μμ
 */
@Builder
public class PropertyValuePairSet {
    @Getter @Singular
    private final ImmutableSet<PropertyValuePair> annotations;

    private PropertyValuePairSet(ImmutableSet<PropertyValuePair> annotations) {
        this.annotations = ImmutableSet.copyOf(groupAnnotationsPerProperty(annotations));
    }

    public Collection<RDFNode> getPropertyValues(Property property) {
        return getPropertyValues(property, this.annotations);
    }

    private Collection<RDFNode> getPropertyValues(Property property, Set<PropertyValuePair> annotationSet) {
        return annotationSet.stream()
                .filter(p -> p.getProperty().equals(property))
                .flatMap(p -> p.getValues().stream())
                .collect(Collectors.toList());
    }


    private Set<PropertyValuePair> groupAnnotationsPerProperty(Set<PropertyValuePair> ungroupedAnnotations) {

        return  ungroupedAnnotations.stream()
                // get list of properties
                .map(an -> an.getProperty())
                .distinct() // remove duplicates
                // for every property get all values
                .map(p -> PropertyValuePair.create(p, getPropertyValues(p, ungroupedAnnotations)) )
                .collect(Collectors.toSet());
    }
}
