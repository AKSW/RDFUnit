package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixNSService;

/**
 * Holds a test case result annotation that will be used when we generate individual results
 *
 * @author Dimitris Kontokostas
 */
public final class ResultAnnotation {
    private final String annotationProperty;
    private final RDFNode annotationValue;

    /**
     * Instantiates a new Result annotation.
     *
     * @param annotationProperty the annotation property
     * @param annotationValue    the annotation value
     */
    public ResultAnnotation(String annotationProperty, RDFNode annotationValue) {
        assert (annotationProperty != null);
        assert (annotationValue != null);

        this.annotationProperty = annotationProperty;
        this.annotationValue = annotationValue;
    }

    /**
     * Serialize this annotation for an extended test case result
     *
     * @param resource the resource
     * @param model    the model
     * @return the resource
     */
    public Resource serializeAsResult(Resource resource, Model model) {
        return resource.addProperty(ResourceFactory.createProperty(getAnnotationProperty()), getAnnotationValue());
    }

    /**
     * Serialize this annotation for exporting a test case.
     *
     * @param model the model
     * @return the resource
     */
    public Resource serializeAsTestCase(Model model) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:ResultAnnotation")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:annotationProperty")), model.createResource(annotationProperty))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:annotationValue")), annotationValue);
    }

    /**
     * Gets the annotation property.
     *
     * @return the annotation property
     */
    public String getAnnotationProperty() {
        return annotationProperty;
    }

    /**
     * Gets the annotation value.
     *
     * @return the annotation value
     */
    public RDFNode getAnnotationValue() {
        return annotationValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResultAnnotation)) {
            return false;
        }

        ResultAnnotation that = (ResultAnnotation) o;

        if (!annotationProperty.equals(that.annotationProperty)) {
            return false;
        }
        if (!annotationValue.equals(that.annotationValue)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = annotationProperty.hashCode();
        result = 31 * result + annotationValue.hashCode();
        return result;
    }
}
