package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class TestAnnotationWriter implements ElementWriter {

    private final TestCaseAnnotation tcAnnotation;

    private TestAnnotationWriter(TestCaseAnnotation tcAnnotation) {
        this.tcAnnotation = tcAnnotation;
    }

    /**
     * <p>create.</p>
     *
     * @param tcAnnotation a {@link org.aksw.rdfunit.model.interfaces.TestCaseAnnotation} object.
     * @return a {@link org.aksw.rdfunit.model.writers.TestAnnotationWriter} object.
     */
    public static TestAnnotationWriter create(TestCaseAnnotation tcAnnotation) {return new TestAnnotationWriter(tcAnnotation);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(tcAnnotation, model);

        resource
                .addProperty(DCTerms.description, tcAnnotation.getDescription())
                .addProperty(RDFUNITv.generated, model.createResource(tcAnnotation.getGenerated().getUri()))
                .addProperty(RDFUNITv.appliesTo, model.createResource(tcAnnotation.getAppliesTo().getUri()))
                .addProperty(RDFUNITv.source, model.createResource(tcAnnotation.getSourceUri()))
                .addProperty(RDFUNITv.testCaseLogLevel, model.createResource(tcAnnotation.getTestCaseLogLevel().getUri()));

        if (tcAnnotation.getAutoGeneratorURI()!= null && !tcAnnotation.getAutoGeneratorURI().trim().isEmpty()) {
            resource.addProperty(RDFUNITv.testGenerator, model.createResource(tcAnnotation.getAutoGeneratorURI()));
        }

        for (String r : tcAnnotation.getReferences()) {
            resource.addProperty(RDFUNITv.references, ResourceFactory.createResource(r));
        }

        for (ResultAnnotation annotation : tcAnnotation.getResultAnnotations()) {
            Resource annRes = ResultAnnotationWriter.create(annotation).write(model);
            resource.addProperty(RDFUNITv.resultAnnotation, annRes);
        }

        for (ResultAnnotation annotation : tcAnnotation.getVariableAnnotations()) {
            Resource annRes = ResultAnnotationWriter.create(annotation).write(model);
            resource.addProperty(RDFUNITv.resultAnnotation, annRes);
        }

        return resource;
    }
}
