package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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
    @NonNull private final String filterSparql;
    @NonNull private final TestCase testCase;

    public TestCaseWithTarget(ShapeTarget target, String filterSparql, TestCase testCase) {
        this.target = target;
        this.filterSparql = filterSparql;
        this.testCase = testCase;
    }

    @Override
    public String getSparqlWhere() {
        return injectTargetInSparql(testCase.getSparqlWhere(), this.target);
    }

    @Override
    public String getSparqlPrevalence() {

        if (testCase.getSparqlPrevalence().trim().isEmpty()
                // skip node targets
                && (! target.getTargetType().equals(ShapeTargetType.NodeTarget))) {

            return "SELECT (count(?this) AS ?total) WHERE { " + target.getPattern() + "}";
        }

        return testCase.getSparqlPrevalence();

    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        // inject SHACL annotations
        // TODO this is not efficient, recreates it on every request but needs major refactoring to improve
        return createFromReferences(testCase.getTestCaseAnnotation(), target.getRelatedOntologyResources());
    }

    @Override
    public Set<ResultAnnotation> getVariableAnnotations() {
        return getTestCaseAnnotation().getVariableAnnotations();
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return testCase.getPrefixDeclarations();
    }

    @Override
    public Resource getElement() {
        return testCase.getElement();
    }


    private String injectSparqlSnippet(String sparqlQuery, String sparqlSnippet) {
        int bracketIndex = sparqlQuery.indexOf('{');
        return sparqlQuery.substring(0,bracketIndex+1) + sparqlSnippet + sparqlQuery.substring(bracketIndex+1);
    }

    private String injectTargetInSparql(String sparqlQuery, ShapeTarget target) {

        String queryWithFilter = injectSparqlSnippet(sparqlQuery, filterSparql);
        String queryWithTarget = injectTarget(queryWithFilter, target);

        return queryWithTarget;
    }



    private static TestCaseAnnotation createFromReferences(TestCaseAnnotation annotation, Collection<Resource> references) {

        ImmutableSet.Builder<String> referencesBuilder = ImmutableSet.builder();
        ImmutableSet<String> finalReferences = referencesBuilder
                .addAll(references.stream().map(Resource::getURI).collect(Collectors.toSet()))
                .addAll(annotation.getReferences()).build();

        ImmutableSet.Builder<ResultAnnotation> resultAnnotationBuilder = ImmutableSet.builder();
        resultAnnotationBuilder
                .addAll(annotation.getResultAnnotations())
                .addAll(annotation.getVariableAnnotations());

        return new TestCaseAnnotation(annotation.getElement(), annotation.getGenerated(), annotation.getAutoGeneratorURI(), annotation.getAppliesTo(), annotation.getSourceUri(), finalReferences, annotation.getDescription(), annotation.getTestCaseLogLevel(), resultAnnotationBuilder.build());
    }

    private static final String thisVar = "\\$this_asdf_1234_qwer";
    private String injectTarget(String sparqlQuery, ShapeTarget target) {
        String changedQuery = sparqlQuery;

        if (target.getTargetType().equals(ShapeTargetType.NodeTarget)) {
            changedQuery = sparqlQuery
                    .replaceFirst("[\\$\\?]this", thisVar )
                    .replaceAll("(?i)BOUND\\s*\\(\\s*[\\$\\?]this\\s*\\)", "BOUND\\("+ thisVar+"\\)")
                    .replaceAll("(?i)GROUP\\s+BY\\s+[\\$\\?]this", "GROUP BY "+ thisVar);

            QuerySolutionMap qsm = new QuerySolutionMap();
            qsm.add("this", target.getNode());
            ParameterizedSparqlString pq = new ParameterizedSparqlString(changedQuery, qsm);
            changedQuery = pq.toString();

            changedQuery = changedQuery
                    .replaceFirst(thisVar, "\\$this")
                    .replaceAll("(?i)BOUND\\("+ thisVar+"\\)", "BOUND\\(\\?this\\)")
                    .replaceAll("(?i)GROUP BY "+ thisVar, "GROUP BY \\$this");
        }

        return injectSparqlSnippet(changedQuery, target.getPattern());

    }

}
