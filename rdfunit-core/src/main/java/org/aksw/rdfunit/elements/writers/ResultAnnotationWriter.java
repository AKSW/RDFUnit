package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.elements.interfaces.ResultAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;

/**
 * Reads a Result annotation
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class ResultAnnotationWriter implements ElementWriter {

    private final ResultAnnotation resultAnnotation;

    private final Resource annotationClazz;
    private final Property propertyP;
    private final Property valueP;
    private final Property varNameP;


    private ResultAnnotationWriter(ResultAnnotation resultAnnotation, Resource annotationClazz, Property propertyP, Property valueP, Property varNameP){
        this.resultAnnotation = resultAnnotation;
        this.annotationClazz = annotationClazz;
        this.propertyP = propertyP;
        this.valueP = valueP;
        this.varNameP = varNameP;
    }

    public static ResultAnnotationWriter createResultAnnotationWriter(ResultAnnotation resultAnnotation) { return createResultAnnotationWriterRut(resultAnnotation); }
    public static ResultAnnotationWriter createResultAnnotationWriterRut(ResultAnnotation resultAnnotation) {
        return new ResultAnnotationWriter(resultAnnotation, RDFUNITv.ResultAnnotation, RDFUNITv.annotationProperty, RDFUNITv.annotationValue, RDFUNITv.annotationValue);}
    public static ResultAnnotationWriter createResultAnnotationWriterShacl(ResultAnnotation resultAnnotation) {
        return new ResultAnnotationWriter(resultAnnotation, SHACL.ResultAnnotation, SHACL.annotationProperty, SHACL.annotationValue, SHACL.annotationVarName);}


    @Override
    public Resource write() {
        Resource resource;

        // keep the original resource if exists
        resource = resultAnnotation.getResource().isPresent() ? resultAnnotation.getResource().get() : ResourceFactory.createResource();

        // rdf:type sh:AnnotationProperty
        resource.addProperty(RDF.type, annotationClazz);

        // annotation property
        resource.addProperty(propertyP, resultAnnotation.getAnnotationProperty());

        if (resultAnnotation.getAnnotationValue().isPresent()) {
            resource.addProperty(valueP, resultAnnotation.getAnnotationValue().get());
        }

        // in RDFUnit vocab the annotation value is the same and the variable name starts with '?'
        String prefix = valueP.getNameSpace().equals(RDFUNITv.namespace) ? "?" : "";

        if (resultAnnotation.getAnnotationVarName().isPresent()) {
            resource.addProperty(varNameP, prefix + resultAnnotation.getAnnotationVarName().get());
        }

        return resource;
    }
}
