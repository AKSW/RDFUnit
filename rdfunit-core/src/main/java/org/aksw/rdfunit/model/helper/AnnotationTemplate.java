package org.aksw.rdfunit.model.helper;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.hp.hpl.jena.rdf.model.Property;

import java.util.HashMap;
import java.util.Set;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 8:47 PM
 */
public final class AnnotationTemplate {
    private final HashMap<Property, Range<Integer>> template = Maps.newHashMap();

    private AnnotationTemplate() { }

    public static AnnotationTemplate create() { return new AnnotationTemplate(); }

    public void addTemplateMin(Property property, int minOccurs) {
        template.put(property, Range.<Integer>atLeast(minOccurs));
    }

    public void addTemplateMax(Property property, int maxOccurs) {
        template.put(property, Range.<Integer>atMost(maxOccurs));
    }

    public void addTemplateMinMax(Property property, int minOccurs, int maxOccurs) {
        template.put(property, Range.<Integer>closed(minOccurs, maxOccurs));
    }

    public Set<Property> getProprtiesAsSet() {
        return template.keySet();
    }

    public boolean existsInTemplate(SimpleAnnotation simpleAnnotation) {
        return template.containsKey(simpleAnnotation.getProperty());
    }

    public boolean isValidAccordingToTemplate(SimpleAnnotation simpleAnnotation) {
        int occurences = simpleAnnotation.getValues().size();
        if (existsInTemplate(simpleAnnotation)) {
            return template.get(simpleAnnotation.getProperty()).contains(occurences);
        }
        return false;
    }
}
