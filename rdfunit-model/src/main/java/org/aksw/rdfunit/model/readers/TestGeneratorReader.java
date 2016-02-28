package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.TestGeneratorImpl;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class TestGeneratorReader implements ElementReader<TestGenerator> {

    private TestGeneratorReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.TestGeneratorReader} object.
     */
    public static TestGeneratorReader create() { return new TestGeneratorReader();}


    /** {@inheritDoc} */
    @Override
    public TestGenerator read(Resource resource) {
        checkNotNull(resource, "Cannot read a TestGenerator from a null resource");

        String description = null;
        String query = null;
        Pattern pattern = null;
        Collection<ResultAnnotation> generatorAnnotations = new ArrayList<>();

        int count = 0; // used to count duplicates

        //description
        for (Statement smt : resource.listProperties(DCTerms.description).toList()) {
            description = smt.getObject().asLiteral().getLexicalForm();
            checkArgument(++count == 1, "Cannot have more than one descriptions '%s' in TestGenerator %s", description, resource.getURI());
        }

        //query
        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlGenerator).toList()) {
            checkArgument(++count == 1, "Cannot have more than one SPARQL queries in TestGenerator %s", resource.getURI());
            query = smt.getObject().asLiteral().getLexicalForm();
        }

        //pattern IRI
        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.basedOnPattern).toList()) {
            checkArgument(++count == 1, "Cannot have more than one pattern references in TestGenerator %s", resource.getURI());
            pattern = PatternService.getPatternFromIRI(smt.getObject().asResource().getURI());

        }

        //annotations
        generatorAnnotations.addAll(resource.listProperties(RDFUNITv.resultAnnotation).toList().stream()
                .map(smt -> ResultAnnotationReader.create().read(smt.getResource()))
                .collect(Collectors.toList()));

        return TestGeneratorImpl.createTAG(resource, description, query, pattern, generatorAnnotations);
    }
}
