package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import org.aksw.rdfunit.elements.implementations.PatternImpl;
import org.aksw.rdfunit.elements.interfaces.Pattern;
import org.aksw.rdfunit.elements.interfaces.PatternParameter;
import org.aksw.rdfunit.elements.interfaces.ResultAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class PatternReader implements ElementReader<Pattern> {

    private PatternReader(){}

    public static PatternReader create() { return new PatternReader();}

    @Override
    public Pattern read(Resource resource) {
        checkNotNull(resource);

        PatternImpl.Builder patternBuilder = new PatternImpl.Builder();

        patternBuilder.setElement(resource);

        /**

         private final String description;
         private final String sparqlWherePattern;
         private final String sparqlPatternPrevalence;
         private final Collection<PatternParameter> parameters;
         private final Collection<ResultAnnotation> annotations;
         */

        // get ID
        for (Statement smt : resource.listProperties(DCTerms.identifier).toList()) {
            patternBuilder.setId(smt.getObject().asLiteral().getString());
        }

        // description
        for (Statement smt : resource.listProperties(DCTerms.description).toList()) {
            patternBuilder.setDescription(smt.getObject().asLiteral().getString());
        }

        // SPARQL where
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlWherePattern).toList()) {
            patternBuilder.setSparqlWherePattern(smt.getObject().asLiteral().getString());
        }

        // SPARQL prevalence
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlPrevalencePattern).toList()) {
            patternBuilder.setSparqlPatternPrevalence(smt.getObject().asLiteral().getString());
        }

        //parameters
        Collection<PatternParameter> patternParameters = new ArrayList<>();
        for (Statement smt : resource.listProperties(RDFUNITv.parameter).toList()) {
            patternParameters.add(PatternParameterReader.create().read(smt.getResource()));
        }
        patternBuilder.setParameters(patternParameters);

        //annotations
        Collection<ResultAnnotation> patternAnnotations = new ArrayList<>();
        for (Statement smt : resource.listProperties(RDFUNITv.resultAnnotation).toList()) {
            patternAnnotations.add(ResultAnnotationReader.create().read(smt.getResource()));
        }
        patternBuilder.setAnnotations(patternAnnotations);

        return patternBuilder.build();
    }
}
