package org.aksw.rdfunit.model.interfaces;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.vocabulary.RLOG;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * <p>TestCaseAnnotation class.</p>
 * TODO: make this an interface and move to Impl
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:40 PM
 * @version $Id: $Id
 */
public class TestCaseAnnotation implements Element {
    private final Resource element;
    private final TestGenerationType generated;
    private final String autoGeneratorURI;
    private final TestAppliesTo appliesTo;
    private final String sourceUri;
    private final Collection<String> references;
    private final String description;
    private final RLOGLevel testCaseLogLevel;
    private final Collection<ResultAnnotation> resultAnnotations;
    private final Collection<ResultAnnotation> variableAnnotations;

    /**
     * <p>Constructor for TestCaseAnnotation.</p>
     *
     * @param element a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param generated a {@link org.aksw.rdfunit.enums.TestGenerationType} object.
     * @param autoGeneratorURI a {@link java.lang.String} object.
     * @param appliesTo a {@link org.aksw.rdfunit.enums.TestAppliesTo} object.
     * @param sourceUri a {@link java.lang.String} object.
     * @param references a {@link java.util.Collection} object.
     * @param description a {@link java.lang.String} object.
     * @param testCaseLogLevel a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     * @param resultAnnotations a {@link java.util.Collection} object.
     */
    public TestCaseAnnotation(Resource element, TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, Collection<String> references, String description, RLOGLevel testCaseLogLevel, Collection<ResultAnnotation> resultAnnotations) {
        this.element = checkNotNull(element);
        this.generated = checkNotNull(generated);
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = checkNotNull(appliesTo);
        this.sourceUri = checkNotNull(sourceUri);
        this.references = checkNotNull(references);
        this.description = checkNotNull(description);
        this.resultAnnotations = new ArrayList<>();
        this.resultAnnotations.addAll(checkNotNull(resultAnnotations));
        // need to instantiate result annotations first
        this.testCaseLogLevel = findAnnotationLevel(testCaseLogLevel);
        this.variableAnnotations = findVariableAnnotations();
    }

    /**
     * <p>Getter for the field <code>generated</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.TestGenerationType} object.
     */
    public TestGenerationType getGenerated() {
        return generated;
    }

    /**
     * <p>Getter for the field <code>autoGeneratorURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAutoGeneratorURI() {
        return autoGeneratorURI;
    }

    /**
     * <p>Getter for the field <code>appliesTo</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.TestAppliesTo} object.
     */
    public TestAppliesTo getAppliesTo() {
        return appliesTo;
    }

    /**
     * <p>Getter for the field <code>sourceUri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSourceUri() {
        return sourceUri;
    }

    /**
     * <p>Getter for the field <code>references</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<String> getReferences() {
        return references;
    }


    /*
     * Get either testCaseLogLevel or generate it from resultAnnotations (and then remove the annotation)
     * */
    private RLOGLevel findAnnotationLevel(RLOGLevel testCaseLogLevel) {

        RLOGLevel logLevel = testCaseLogLevel;

        ResultAnnotation pointer = null;
        for (ResultAnnotation annotation : resultAnnotations) {
            Property p = RLOG.level;
            if (annotation.getAnnotationProperty().toString().equals(RLOG.level.toString())) {
                pointer = annotation;
            }
        }
        if (pointer != null) {
            if (logLevel == null) {// Get new value only if testCaseLogLevel doesn't exists
                logLevel = RLOGLevel.resolve(pointer.getAnnotationValue().get().toString());
            }
            resultAnnotations.remove(pointer); // remove now that we have testCaseLogLevel
        }

        return logLevel;
    }

    private Collection<ResultAnnotation> findVariableAnnotations() {

        ArrayList<ResultAnnotation> variableAnnotations = new ArrayList<>();

        for (ResultAnnotation annotation : resultAnnotations) {
            if (annotation.getAnnotationVarName().isPresent()) {
                variableAnnotations.add(annotation);
            }
        }
        for (ResultAnnotation variableAnnotation: variableAnnotations) {
            resultAnnotations.remove(variableAnnotation);
        }

        return variableAnnotations;
    }

    /**
     * <p>Getter for the field <code>testCaseLogLevel</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     */
    public RLOGLevel getTestCaseLogLevel() {
        return testCaseLogLevel;
    }

    /**
     * <p>Getter for the field <code>resultAnnotations</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ResultAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }

    /**
     * <p>Getter for the field <code>variableAnnotations</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ResultAnnotation> getVariableAnnotations() {
        return variableAnnotations;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }
}
