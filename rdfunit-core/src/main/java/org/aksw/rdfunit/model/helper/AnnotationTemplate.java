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
     * @param property a {@link com.hp.hpl.jena.rdf.model.Property} object.
     * @param minOccurs a int.
     */
    public void addTemplateMin(Property property, int minOccurs) {
        template.put(property, Range.<Integer>atLeast(minOccurs));
    }

    /**
     * <p>addTemplateMax.</p>
     *
     * @param property a {@link com.hp.hpl.jena.rdf.model.Property} object.
     * @param maxOccurs a int.
     */
    public void addTemplateMax(Property property, int maxOccurs) {
        template.put(property, Range.<Integer>atMost(maxOccurs));
    }

    /**
     * <p>addTemplateMinMax.</p>
     *
     * @param property a {@link com.hp.hpl.jena.rdf.model.Property} object.
     * @param minOccurs a int.
     * @param maxOccurs a int.
     */
    public void addTemplateMinMax(Property property, int minOccurs, int maxOccurs) {
        template.put(property, Range.<Integer>closed(minOccurs, maxOccurs));
    }

    /**
     * <p>getProprtiesAsSet.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Property> getProprtiesAsSet() {
        return template.keySet();
    }

    /**
     * <p>existsInTemplate.</p>
     *
     * @param simpleAnnotation a {@link org.aksw.rdfunit.model.helper.SimpleAnnotation} object.
     * @return a boolean.
     */
    public boolean existsInTemplate(SimpleAnnotation simpleAnnotation) {
        return template.containsKey(simpleAnnotation.getProperty());
    }

    /**
     * <p>isValidAccordingToTemplate.</p>
     *
     * @param simpleAnnotation a {@link org.aksw.rdfunit.model.helper.SimpleAnnotation} object.
     * @return a boolean.
     */
    public boolean isValidAccordingToTemplate(SimpleAnnotation simpleAnnotation) {
        int occurences = simpleAnnotation.getValues().size();
        if (existsInTemplate(simpleAnnotation)) {
            return template.get(simpleAnnotation.getProperty()).contains(occurences);
        }
        return false;
    }
}
