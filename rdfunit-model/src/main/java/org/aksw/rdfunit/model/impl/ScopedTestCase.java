package org.aksw.rdfunit.model.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.apache.jena.rdf.model.Resource;

/**
 * Used mainly for SHACL in order to inject a scope from a shape to a sparql constraint
 *
 * @author Dimitris Kontokostas
 * @since 14/2/2016 12:55 μμ
 */
@Builder
@ToString
public class ScopedTestCase implements TestCase {

    @NonNull private final ShapeScope scope;
    @NonNull private final String filterSpqrql;
    @NonNull private final TestCase testCase;


    @Override
    public String getSparqlWhere() {
        return injectScopeInSparql(testCase.getSparqlWhere(), this.scope);
    }

    @Override
    public String getSparqlPrevalence() {
        // FIXME we need scope here too
        return testCase.getSparqlPrevalence();
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        return testCase.getTestCaseAnnotation();
    }

    @Override
    public Resource getElement() {
        return testCase.getElement();
    }

    private String injectScopeInSparql(String sparqlQuery, ShapeScope scope) {
        // FIXME good for now
        int bracketIndex = sparqlQuery.indexOf('{');
        return sparqlQuery.substring(0,bracketIndex+1) + scope.getPattern() + filterSpqrql + sparqlQuery.substring(bracketIndex+1);
    }

}
