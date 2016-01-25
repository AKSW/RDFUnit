package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.*;

/**
 * @author Dimitris Kontokostas
 * @since 12/1/2016 11:50 μμ
 */
public class SimpleAnnotationSet {
    Set<SimpleAnnotation> annotations = new HashSet<>();

    private SimpleAnnotationSet() {}

    public static SimpleAnnotationSet create() {return new SimpleAnnotationSet();}

    public void add(Property property, RDFNode value) {
        add(property, Collections.singletonList(value));
    }

    public void add(Property property, Collection<RDFNode> values) {
        annotations.add(SimpleAnnotation.create(property, values));
    }

    public void reset() {
        annotations.clear();
    }

    /**
     * Returns an Immutable Set of the existing annotations
     */
    public Set<SimpleAnnotation> getAnnotations() {
        Set<Property> properties = getAnnotationProperties();
        if (properties.size() == annotations.size()) {
            return ImmutableSet.copyOf(annotations);
        }

        Set<SimpleAnnotation> annotationsCopy = new HashSet<>();
        for (Property p: properties) {
            annotationsCopy.add(SimpleAnnotation.create(p, getPropertyValues(p)));
        }
        return new ImmutableSet.Builder<SimpleAnnotation>().addAll(annotationsCopy).build();
    }

    private Set<Property> getAnnotationProperties() {
        Set<Property> properties = new HashSet<>();
        for (SimpleAnnotation an: annotations) {
            properties.add(an.getProperty());
        }
        return properties;
    }

    private Collection<RDFNode> getPropertyValues(Property property) {
        Set<RDFNode> values = new HashSet<>();
        for (SimpleAnnotation an: annotations) {
            if (an.getProperty().equals(property));
            values.addAll(an.getValues());
        }
        return values;
    }
}
