package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.Shape;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:15 PM
 * @version $Id: $Id
 */
public class ShapeReader implements ElementReader<Shape> {

    private ShapeReader() {
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternReader} object.
     */
    public static ShapeReader create() {
        return new ShapeReader();
    }

    /** {@inheritDoc} */
    @Override
    public Shape read(Resource resource) {
        checkNotNull(resource);

        return null;

    /*
        // get ID
        count = 0;
        for (Statement smt : resource.listProperties(DCTerms.identifier).toList()) {
            checkArgument(++count == 1, "Cannot have more than one identifier in Pattern %s", resource.getURI());
            patternBuilder.setId(smt.getObject().asLiteral().getLexicalForm());
        }

        // description
        count = 0;
        for (Statement smt : resource.listProperties(DCTerms.description).toList()) {
            checkArgument(++count == 1, "Cannot have more than one description in Pattern %s", resource.getURI());
            patternBuilder.setDescription(smt.getObject().asLiteral().getLexicalForm());
        }

        // SPARQL where
        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlWherePattern).toList()) {
            checkArgument(++count == 1, "Cannot have more than one SPARQL query in Pattern %s", resource.getURI());
            patternBuilder.setSparqlWherePattern(smt.getObject().asLiteral().getLexicalForm());
        }

        // SPARQL prevalence
        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlPrevalencePattern).toList()) {
            checkArgument(++count == 1, "Cannot have more than one prevalence query in Pattern %s", resource.getURI());
            patternBuilder.setSparqlPatternPrevalence(smt.getObject().asLiteral().getLexicalForm());
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
        */
    }
}

