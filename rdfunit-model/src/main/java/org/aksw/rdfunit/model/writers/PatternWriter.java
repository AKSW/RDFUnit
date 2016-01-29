package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
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
public final class PatternWriter implements ElementWriter {

    private final Pattern pattern;

    private PatternWriter(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * <p>create.</p>
     *
     * @param pattern a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @return a {@link org.aksw.rdfunit.model.writers.PatternWriter} object.
     */
    public static PatternWriter create(Pattern pattern) {return new PatternWriter(pattern);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(pattern, model);

        resource
                .addProperty(RDF.type, RDFUNITv.Pattern)
                .addProperty(DCTerms.identifier, pattern.getId())
                .addProperty(DCTerms.description, pattern.getDescription())
                .addProperty(RDFUNITv.sparqlWherePattern, pattern.getSparqlWherePattern());

        if (pattern.getSparqlPatternPrevalence().isPresent()) {
            resource.addProperty(RDFUNITv.sparqlPrevalencePattern, pattern.getSparqlPatternPrevalence().get());
        }

        for (PatternParameter patternParameter: pattern.getParameters()) {
            Resource parameter = PatternParameterWriter.create(patternParameter).write(model);
            resource.addProperty(RDFUNITv.parameter, parameter);
        }

        for (ResultAnnotation resultAnnotation: pattern.getResultAnnotations()) {
            Resource annotationResource = ResultAnnotationWriter.create(resultAnnotation).write(model);
            resource.addProperty(RDFUNITv.resultAnnotation, annotationResource);
        }

        return resource;
    }
}
