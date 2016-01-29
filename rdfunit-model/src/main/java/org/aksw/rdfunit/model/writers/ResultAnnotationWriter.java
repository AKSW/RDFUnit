package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Reads a Result annotation
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
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

    /**
     * <p>create.</p>
     *
     * @param resultAnnotation a {@link org.aksw.rdfunit.model.interfaces.ResultAnnotation} object.
     * @return a {@link org.aksw.rdfunit.model.writers.ResultAnnotationWriter} object.
     */
    public static ResultAnnotationWriter create(ResultAnnotation resultAnnotation) { return createResultAnnotationWriterRut(resultAnnotation); }
    /**
     * <p>createResultAnnotationWriterRut.</p>
     *
     * @param resultAnnotation a {@link org.aksw.rdfunit.model.interfaces.ResultAnnotation} object.
     * @return a {@link org.aksw.rdfunit.model.writers.ResultAnnotationWriter} object.
     */
    public static ResultAnnotationWriter createResultAnnotationWriterRut(ResultAnnotation resultAnnotation) {
        return new ResultAnnotationWriter(resultAnnotation, RDFUNITv.ResultAnnotation, RDFUNITv.annotationProperty, RDFUNITv.annotationValue, RDFUNITv.annotationValue);}
    /**
     * <p>createResultAnnotationWriterShacl.</p>
     *
     * @param resultAnnotation a {@link org.aksw.rdfunit.model.interfaces.ResultAnnotation} object.
     * @return a {@link org.aksw.rdfunit.model.writers.ResultAnnotationWriter} object.
     */
    public static ResultAnnotationWriter createResultAnnotationWriterShacl(ResultAnnotation resultAnnotation) {
        return new ResultAnnotationWriter(resultAnnotation, SHACL.ResultAnnotation, SHACL.annotationProperty, SHACL.annotationValue, SHACL.annotationVarName);}


    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(resultAnnotation, model);

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
