package org.aksw.rdfunit.model.impl.shacl;

import lombok.EqualsAndHashCode;
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

@EqualsAndHashCode(exclude = {"element"})
public class AlwaysFailingTestCase implements TestCase, TargetBasedTestCase {

    public static final String AlwaysFailingTestCasePrefix = "http://rdfunit.aksw.org/aftc/";

    private final ShapeTarget target;
    private final Resource element;
    private final String sparqlWhere;

    public AlwaysFailingTestCase(ShapeTarget target){
        this.target = target;
        this.element = ResourceFactory.createResource(AlwaysFailingTestCase.AlwaysFailingTestCasePrefix + JenaUUID.generate().asString());
        this.sparqlWhere = "{ " + target.getPattern() + " FILTER NOT EXISTS {?this <http://example.org/some/non/existing/property> 9876545432} }";
    }

    @Override
    public String getSparqlWhere() {
        return sparqlWhere;
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
