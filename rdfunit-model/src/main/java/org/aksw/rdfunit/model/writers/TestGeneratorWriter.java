package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class TestGeneratorWriter implements ElementWriter {

    private final TestGenerator testGenerator;

    private TestGeneratorWriter(TestGenerator testGenerator) {
        this.testGenerator = testGenerator;
    }

    /**
     * <p>create.</p>
     *
     * @param testGenerator a {@link org.aksw.rdfunit.model.interfaces.TestGenerator} object.
     * @return a {@link org.aksw.rdfunit.model.writers.TestGeneratorWriter} object.
     */
    public static TestGeneratorWriter create(TestGenerator testGenerator) {return new TestGeneratorWriter(testGenerator);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(testGenerator, model);

        resource
                .addProperty(RDF.type, RDFUNITv.TestGenerator)
                .addProperty(DCTerms.description, testGenerator.getDescription())
                .addProperty(RDFUNITv.sparqlGenerator, testGenerator.getQuery())
                .addProperty(RDFUNITv.basedOnPattern, ElementWriter.copyElementResourceInModel(testGenerator.getPattern(), model));


        for (ResultAnnotation resultAnnotation: testGenerator.getAnnotations()) {
            Resource annotationResource = ResultAnnotationWriter.create(resultAnnotation).write(model);
            resource.addProperty(RDFUNITv.resultAnnotation, annotationResource);
        }

        return resource;
    }
}
