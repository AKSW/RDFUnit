package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixService;

/**
 * Holds a test case result annotation that will be used when we generate individual results
 */
public class ResultAnnotation {
    private final String annotationProperty;
    private final RDFNode annotationValue;

    public ResultAnnotation(String annotationProperty, RDFNode annotationValue) {
        this.annotationProperty = annotationProperty;
        this.annotationValue = annotationValue;
    }

    public Resource serializeAsResult(Resource resource, Model model) {
        return resource.addProperty(ResourceFactory.createProperty(getAnnotationProperty()), getAnnotationValue());
    }

    public Resource serializeAsTestCase(Model model) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("rut") + "ResultAnnotation"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "annotationProperty"), model.createResource(annotationProperty))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rut"), "annotationValue"), annotationValue);
    }

    public String getAnnotationProperty() {
        return annotationProperty;
    }

    public RDFNode getAnnotationValue() {
        return annotationValue;
    }
}
