package org.aksw.rdfunit.model.impl.shacl;

import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.utils.CommonNames;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.uuid.JenaUUID;

import java.util.Collection;
import java.util.Collections;

public class AlwaysFailingTestCase implements TestCase, TargetBasedTestCase {

    static final String AlwaysFailingTestCasePrefix = "http://rdfunit.aksw.org/aftc/";

    private final ShapeTarget target;
    private final Resource element;

    AlwaysFailingTestCase(ShapeTarget target){
        this.target = target;
        this.element = ResourceFactory.createResource(AlwaysFailingTestCase.AlwaysFailingTestCasePrefix + JenaUUID.generate().asString());
    }

    @Override
    public String getSparqlWhere() {
        return "{ " + target.getPattern() + " }";
    }

    @Override
    public String getSparqlPrevalence() {
        return "";
    }

    @Override
    public RDFNode getFocusNode(QuerySolution solution){
        return solution.get(CommonNames.This);
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        return TestCaseAnnotation.Empty;
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return Collections.emptySet();
    }

    @Override
    public Resource getElement() {
        return element;
    }

    @Override
    public ShapeTarget getTarget() {
        return target;
    }
}
