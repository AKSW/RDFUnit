package org.aksw.rdfunit.model.interfaces;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.vocabulary.RLOG;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
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
    private final ImmutableSet<ResultAnnotation> resultAnnotations;
    private final ImmutableSet<ResultAnnotation> variableAnnotations;

    public TestCaseAnnotation(Resource element, TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, Set<String> references, String description, RLOGLevel testCaseLogLevel, Set<ResultAnnotation> resultAnnotations) {
        this.element = checkNotNull(element);
        this.generated = checkNotNull(generated);
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = checkNotNull(appliesTo);
        this.sourceUri = checkNotNull(sourceUri);
        this.references = ImmutableSet.copyOf(checkNotNull(references));
        this.description = checkNotNull(description);

        this.testCaseLogLevel = findAnnotationLevel(testCaseLogLevel, checkNotNull(resultAnnotations));
        this.variableAnnotations = ImmutableSet.copyOf(findVariableAnnotations(resultAnnotations));
        this.resultAnnotations = ImmutableSet.copyOf(findNonVariableAnnotations(resultAnnotations));
    }

    public Set<String> getReferences() {
        return references;
    }

    public Set<ResultAnnotation> getResultAnnotations() {
        return resultAnnotations;
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

}
