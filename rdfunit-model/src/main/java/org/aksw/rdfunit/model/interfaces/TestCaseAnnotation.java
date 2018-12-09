package org.aksw.rdfunit.model.interfaces;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.RLOG;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: make this an interface and move to Impl
 * @author Dimitris Kontokostas
 * @since 1/3/14 3:40 PM
 */
@ToString
@EqualsAndHashCode(exclude = "element")
public class TestCaseAnnotation implements Element {
    @Getter private final Resource element;
    @Getter private final TestGenerationType generated;
    @Getter private final String autoGeneratorURI;
    @Getter private final TestAppliesTo appliesTo;
    @Getter private final String sourceUri;
    private final ImmutableSet<String> references;
    @Getter private final String description;
    @Getter private final RLOGLevel testCaseLogLevel;
    private final HashSet<ResultAnnotation> resultAnnotations;      //TODO changed to mutable, since i want to add annotaions later
    private final HashSet<ResultAnnotation> variableAnnotations;

    public TestCaseAnnotation(Resource element, TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, Set<String> references, String description, RLOGLevel testCaseLogLevel, Set<ResultAnnotation> resultAnnotations) {
        this.element = checkNotNull(element);
        this.generated = checkNotNull(generated);
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = checkNotNull(appliesTo);
        this.sourceUri = checkNotNull(sourceUri);
        this.references = ImmutableSet.copyOf(checkNotNull(references));
        this.description = checkNotNull(description);

        this.testCaseLogLevel = findAnnotationLevel(testCaseLogLevel, checkNotNull(resultAnnotations));
        this.variableAnnotations = Sets.newHashSet(findVariableAnnotations(resultAnnotations));
        this.resultAnnotations = Sets.newHashSet(findNonVariableAnnotations(resultAnnotations));
    }

    public Set<String> getReferences() {
        return references;
    }

    public Set<ResultAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }

    public void addResultAnnotations(Collection<ResultAnnotation> anno){
        this.variableAnnotations.addAll(findVariableAnnotations(anno));
        this.resultAnnotations.addAll(findNonVariableAnnotations(anno));
    }

    public Set<ResultAnnotation> getVariableAnnotations() {
        return variableAnnotations;
    }

    /**
     * Get either testCaseLogLevel or generate it from resultAnnotations
     **/
    private static RLOGLevel findAnnotationLevel(RLOGLevel testCaseLogLevel, Collection<ResultAnnotation> givenAnnotations) {

        for (ResultAnnotation annotation : givenAnnotations) {
            if (annotation.getAnnotationProperty().equals(RLOG.level)) {
                return RLOGLevel.resolve(annotation.getAnnotationValue().get().toString());
            }
        }
        // return default
        return testCaseLogLevel;
    }

    private static Collection<ResultAnnotation> findVariableAnnotations(Collection<ResultAnnotation> givenAnnotations) {

        return givenAnnotations.stream()
                .filter(a -> a.getAnnotationVarName().isPresent())
                .collect(Collectors.toList());
    }

    private static Collection<ResultAnnotation> findNonVariableAnnotations(Collection<ResultAnnotation> givenAnnotations) {

        return givenAnnotations.stream()
                .filter(a -> !a.getAnnotationVarName().isPresent())
                .filter(a -> !a.getAnnotationProperty().equals(RLOG.level))
                .collect(Collectors.toList());
    }

    public static TestCaseAnnotation Empty = new TestCaseAnnotation(
        ResourceFactory.createProperty("urn:uuid:resource:empty"),
            TestGenerationType.ManuallyGenerated,
            "urn:uuid:resource:dummy",
            TestAppliesTo.Application,
            "urn:uuid:resource:dummy",
            Collections.emptySet(),
            "An empty dummy annotation. Most likely used inside a TestCaseGroup. Please refer to the TestCaseAnnotations of the individual TestCases.",
            RLOGLevel.OFF,
            Collections.emptySet()
    );
}
