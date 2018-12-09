package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
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

public class TestCaseGroupOr implements TestCaseGroup {


    private final Resource resource;
    private final ImmutableSet<GenericTestCase> testCases;

    public TestCaseGroupOr(@NonNull Set<? extends GenericTestCase> testCases) {
        assert(! testCases.isEmpty());
        this.resource = ResourceFactory.createProperty(JenaUtils.getUniqueIri());
        //adding for all members of the group the relevant result annotation
        testCases.forEach(tc -> TestCaseGroup.addTestCaseGroupResultAnnotation(tc.getElement(), SHACL.or, this.resource, tc.getTestCaseAnnotation()));
        this.testCases = ImmutableSet.copyOf(testCases);
    }

    @Override
    public Set<GenericTestCase> getTestCases() {
        return this.testCases;
    }

    @Override
    public SHACL.LogicalConstraint getLogicalOperator() {
        return SHACL.LogicalConstraint.or;
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        //TODO not sure what to return here, for now I added an empty TestCaseAnnotation
        return TestCaseAnnotation.Empty;
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
