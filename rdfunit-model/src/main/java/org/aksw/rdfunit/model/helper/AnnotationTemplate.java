package org.aksw.rdfunit.model.helper;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import org.apache.jena.rdf.model.Property;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 8/28/15 8:47 PM
 */
public final class AnnotationTemplate {
    private final HashMap<Property, Range<Integer>> template = Maps.newHashMap();

    private AnnotationTemplate() { }

    public static AnnotationTemplate create() { return new AnnotationTemplate(); }

    public void addTemplateMin(Property property, int minOccurs) {
        template.put(property, Range.atLeast(minOccurs));
    }

    public void addTemplateMax(Property property, int maxOccurs) {
        template.put(property, Range.atMost(maxOccurs));
    }

    public void addTemplateMinMax(Property property, int minOccurs, int maxOccurs) {
        template.put(property, Range.closed(minOccurs, maxOccurs));
    }

    public Set<Property> getPropertiesAsSet() {
        return template.keySet();
    }

    public boolean existsInTemplate(PropertyValuePair propertyValuePair) {
        return template.containsKey(propertyValuePair.getProperty());
    }

    public boolean isValidAccordingToTemplate(PropertyValuePair propertyValuePair) {
        int occurrences = propertyValuePair.getValues().size();
        if (existsInTemplate(propertyValuePair)) {
            return template.get(propertyValuePair.getProperty()).contains(occurrences);
        }
        return false;
    }
}
