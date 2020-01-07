package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.impl.ResultAnnotationImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.utils.CommonNames;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Collection;
import java.util.Optional;
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
@EqualsAndHashCode(exclude={"element"})
public class TestCaseWithTarget implements TestCase, TargetBasedTestCase {

    @Getter @NonNull private final ShapeTarget target;
    @Getter @NonNull private final String filterSparql;
    @Getter @NonNull private final TestCase testCase;
    @Getter @NonNull private final Resource element = ResourceFactory.createProperty(JenaUtils.getUniqueIri());

    public TestCaseWithTarget(ShapeTarget target, String filterSparql, TestCase testCase) {
        this.target = target;
        this.filterSparql = filterSparql;
        this.testCase = testCase;
    }

    @Override
    public String getSparqlWhere() {
        String withFocus = testCase.getSparqlWhere();
        if(target instanceof ShapeTargetValueShape) {
            // inserting the focus node in the group statement
            withFocus = withFocus.replaceFirst("(?i)GROUP BY\\s+\\?" + CommonNames.This, "GROUP BY ?" + CommonNames.This + " ?focusNode");
        }
        return injectTargetInSparql(withFocus, this.target);
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
    public RDFNode getFocusNode(QuerySolution solution) {

        String focusVar = getVariableAnnotations().stream()
                .filter(ra -> ra.getAnnotationProperty().equals(SHACL.focusNode))
                .map(ResultAnnotation::getAnnotationVarName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(CommonNames.This);
        return solution.get(focusVar);
    }

    @Override
    public TestCaseAnnotation getTestCaseAnnotation() {
        // inject SHACL annotations
        // TODO this is not efficient, recreates it on every request but needs major refactoring to improve
        return createFromReferences(testCase.getTestCaseAnnotation(), target.getRelatedOntologyResources());
    }

    @Override
    public Set<ResultAnnotation> getVariableAnnotations() {
        ImmutableSet.Builder<ResultAnnotation> builder = ImmutableSet.builder();
        if(ShapeTargetValueShape.class.isAssignableFrom(target.getClass())) {

            getTestCaseAnnotation().getVariableAnnotations().forEach(va -> {
                if(! va.getAnnotationProperty().equals(SHACL.focusNode))
                    builder.add(va);
            });
            // add also a new variable annotation for the focus node
            builder.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.focusNode)
                    .setVariableName(SHACL.focusNode.getLocalName()).build());
        }
        else{
            builder.addAll(getTestCaseAnnotation().getVariableAnnotations());
        }
        return builder.build();
    }

    @Override
    public Collection<PrefixDeclaration> getPrefixDeclarations() {
        return testCase.getPrefixDeclarations();
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

        return new TestCaseAnnotation(
                annotation.getElement(),
                annotation.getGenerated(),
                annotation.getAutoGeneratorURI(),
                annotation.getAppliesTo(),
                annotation.getSourceUri(),
                finalReferences,
                annotation.getDescription(),
                annotation.getTestCaseLogLevel(),
                resultAnnotationBuilder.build()
        );
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
            qsm.add(CommonNames.This, target.getNode());
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
