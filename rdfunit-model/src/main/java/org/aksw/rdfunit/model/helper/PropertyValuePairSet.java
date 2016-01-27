package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 12/1/2016 11:50 μμ
 */
public class PropertyValuePairSet {
    Set<PropertyValuePair> annotations = new HashSet<>();

    private PropertyValuePairSet() {}

    public static PropertyValuePairSet create() {return new PropertyValuePairSet();}

    public void add(Property property, RDFNode value) {
        add(property, Collections.singletonList(value));
    }

    public void add(Property property, Collection<RDFNode> values) {
        annotations.add(PropertyValuePair.create(property, values));
    }

    public void reset() {
        annotations.clear();
    }

    /**
     * Returns an Immutable Set of the existing annotations
     */
    public Set<PropertyValuePair> getAnnotations() {
        Set<Property> properties = getAnnotationProperties();
        if (properties.size() == annotations.size()) {
            return ImmutableSet.copyOf(annotations);
        }

        Set<PropertyValuePair> annotationsCopy = new HashSet<>();
        for (Property p: properties) {
            annotationsCopy.add(PropertyValuePair.create(p, getPropertyValues(p)));
        }
        return new ImmutableSet.Builder<PropertyValuePair>().addAll(annotationsCopy).build();
    }

    private Set<Property> getAnnotationProperties() {
        Set<Property> properties = new HashSet<>();
        for (PropertyValuePair an: annotations) {
            properties.add(an.getProperty());
        }
        return properties;
    }

    private Collection<RDFNode> getPropertyValues(Property property) {
        Set<RDFNode> values = new HashSet<>();
        for (PropertyValuePair an: annotations) {
            if (an.getProperty().equals(property));
            values.addAll(an.getValues());
        }
        return values;
    }
}
