package org.aksw.rdfunit.model.impl.shacl;

import lombok.NonNull;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseGroup;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TestCaseGroupImpl implements TestCaseGroup {

    private final Resource resource;
    private final Set<TestCase> testCases;
    private final SHACL.LogicalConstraint operator;

    public TestCaseGroupImpl(@NonNull Set<TestCase> testCases, @NonNull SHACL.LogicalConstraint operator) {
        assert(! testCases.isEmpty());
        this.testCases = testCases;
        this.operator = operator;
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
    }

    public TestCaseGroupImpl( @NonNull Set<TestCase> testCases) {
        this(testCases, SHACL.LogicalConstraint.atomic);
    }

    @Override
    public Set<TestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return operator;
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        //TODO
        return testCases.iterator().next().getTestCaseAnnotation();
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return testCases.stream().flatMap(t -> t.getPrefixDeclarations().stream()).collect(Collectors.toSet());
    }

    @Override
    public Resource getElement() {
        return this.resource;
    }
}
