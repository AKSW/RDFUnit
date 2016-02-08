package org.aksw.rdfunit.model.helper;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import org.apache.jena.rdf.model.Property;

import java.util.HashMap;
import java.util.Set;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 8:47 PM
 * @version $Id: $Id
 */
public final class AnnotationTemplate {
    private final HashMap<Property, Range<Integer>> template = Maps.newHashMap();

    private AnnotationTemplate() { }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.helper.AnnotationTemplate} object.
     */
    public static AnnotationTemplate create() { return new AnnotationTemplate(); }

    /**
     * <p>addTemplateMin.</p>
     *
     * @param property a {@link org.apache.jena.rdf.model.Property} object.
     * @param minOccurs a int.
     */
    public void addTemplateMin(Property property, int minOccurs) {
        template.put(property, Range.atLeast(minOccurs));
    }

    /**
     * <p>addTemplateMax.</p>
     *
     * @param property a {@link org.apache.jena.rdf.model.Property} object.
     * @param maxOccurs a int.
     */
    public void addTemplateMax(Property property, int maxOccurs) {
        template.put(property, Range.atMost(maxOccurs));
    }

    /**
     * <p>addTemplateMinMax.</p>
     *
     * @param property a {@link org.apache.jena.rdf.model.Property} object.
     * @param minOccurs a int.
     * @param maxOccurs a int.
     */
    public void addTemplateMinMax(Property property, int minOccurs, int maxOccurs) {
        template.put(property, Range.closed(minOccurs, maxOccurs));
    }

    /**
     * <p>getPropertiesAsSet.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Property> getPropertiesAsSet() {
        return template.keySet();
    }

    /**
     * <p>existsInTemplate.</p>
     *
     * @param propertyValuePair a {@link PropertyValuePair} object.
     * @return a boolean.
     */
    public boolean existsInTemplate(PropertyValuePair propertyValuePair) {
        return template.containsKey(propertyValuePair.getProperty());
    }

    /**
     * <p>isValidAccordingToTemplate.</p>
     *
     * @param propertyValuePair a {@link PropertyValuePair} object.
     * @return a boolean.
     */
    public boolean isValidAccordingToTemplate(PropertyValuePair propertyValuePair) {
        int occurrences = propertyValuePair.getValues().size();
        if (existsInTemplate(propertyValuePair)) {
            return template.get(propertyValuePair.getProperty()).contains(occurrences);
        }
        return false;
    }
}
