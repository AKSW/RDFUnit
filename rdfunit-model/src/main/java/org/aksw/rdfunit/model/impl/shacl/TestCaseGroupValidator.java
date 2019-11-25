package org.aksw.rdfunit.model.impl.shacl;

import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.Validator;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class TestCaseGroupValidator implements Validator {

    private final Resource resource = ResourceFactory.createResource("http://rdfunit.aksw.org/testCaseGroup/Validator");

    private TestCaseGroupValidator(){ }

    @Override
    public Optional<Literal> getDefaultMessage() {
        return Optional.empty();
    }

    @Override
    public String getSparqlQuery() {
        return "SELECT ?this WHERE { ?this <http://example.org/non/existing> ?lkjhgfd . }";
    }

    @Override
    public Set<PrefixDeclaration> getPrefixDeclarations() {
        return Collections.emptySet();
    }

    @Override
    public Resource getElement() {
        return this.resource;
    }

    private static TestCaseGroupValidator singleton = new TestCaseGroupValidator();
    static TestCaseGroupValidator instance(){ return singleton; }

    public static Set<ResultAnnotation> getGroupResultAnnotations(Shape shape, SHACL.LogicalConstraint operator){
        Query query = QueryFactory.create(instance().getSparqlQuery());
        return ResultAnnotationParser.builder()
                .query(query)
                .validator(instance())
                .shape(shape)
                .logicalConstraint(operator)
                .build()
                .getResultAnnotations();
    }
}
