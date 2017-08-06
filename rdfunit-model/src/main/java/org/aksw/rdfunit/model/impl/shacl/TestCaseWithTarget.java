package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;

/**
 * Used mainly for SHACL in order to inject a target from a shape to a sparql constraint
 *
 * @author Dimitris Kontokostas
 * @since 14/2/2016 12:55 μμ
 */
@Builder
@ToString
public class TestCaseWithTarget implements TestCase {

    @NonNull private final ShapeTarget target;
    @NonNull private final String filterSpqrql;
    @NonNull private final TestCase testCase;

    @Override
    public String getSparqlWhere() {
        return injectTargetInSparql(testCase.getSparqlWhere(), this.target);
    }

    @Override
    public String getSparqlPrevalence() {
        // FIXME we need target here too
        return testCase.getSparqlPrevalence();
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        return testCase.getTestCaseAnnotation();
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return testCase.getPrefixDeclarations();
    }

    @Override
    public Resource getElement() {
        return testCase.getElement();
    }

    private String injectTargetInSparql(String sparqlQuery, ShapeTarget target) {
        // FIXME good for now
        int bracketIndex = sparqlQuery.indexOf('{');
        return sparqlQuery.substring(0,bracketIndex+1) + target.getPattern() + filterSpqrql + sparqlQuery.substring(bracketIndex+1);
    }

}
