package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>TestCaseAnnotation class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:40 PM
 * @version $Id: $Id
 */
public class TestCaseAnnotation {
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
     * @param generated a {@link org.aksw.rdfunit.enums.TestGenerationType} object.
     * @param autoGeneratorURI a {@link java.lang.String} object.
     * @param appliesTo a {@link org.aksw.rdfunit.enums.TestAppliesTo} object.
     * @param sourceUri a {@link java.lang.String} object.
     * @param references a {@link java.util.Collection} object.
     * @param description a {@link java.lang.String} object.
     * @param testCaseLogLevel a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     * @param resultAnnotations a {@link java.util.Collection} object.
     */
    public TestCaseAnnotation(TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, Collection<String> references, String description, RLOGLevel testCaseLogLevel, Collection<ResultAnnotation> resultAnnotations) {
        this.generated = generated;
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = appliesTo;
        this.sourceUri = sourceUri;
        this.references = references;
        this.description = description;
        this.resultAnnotations = new ArrayList<>();
        this.resultAnnotations.addAll(resultAnnotations);
        // need to instantiate result annotations first
        this.testCaseLogLevel = findAnnotationLevel(testCaseLogLevel);
        this.variableAnnotations = findVariableAnnotations();
    }

    /**
     * <p>serialize.</p>
     *
     * @param resource a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     */
    public Resource serialize(Resource resource, Model model) {
        resource
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("dcterms:description")), description)
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:generated")), model.createResource(getGenerated().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testGenerator")), model.createResource(getAutoGeneratorURI()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:appliesTo")), model.createResource(getAppliesTo().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:source")), model.createResource(getSourceUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:testCaseLogLevel")), model.createResource(getTestCaseLogLevel().getUri()));

        for (String r : getReferences()) {
            resource.addProperty(model.createProperty(PrefixNSService.getURIFromAbbrev("rut:references")), ResourceFactory.createResource(r));
        }

        for (ResultAnnotation annotation : resultAnnotations) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:resultAnnotation")), annotation.serializeAsTestCase(model));
        }

        for (ResultAnnotation annotation : variableAnnotations) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:resultAnnotation")), annotation.serializeAsTestCase(model));
        }

        return resource;
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
            if (annotation.getAnnotationProperty().equals(PrefixNSService.getURIFromAbbrev("rlog:level"))) {
                pointer = annotation;
            }
        }
        if (pointer != null) {
            if (logLevel == null) {// Get new value only if testCaseLogLevel doesn't exists
                logLevel = RLOGLevel.resolve(pointer.getAnnotationValue().toString());
            }
            resultAnnotations.remove(pointer); // remove now that we have testCaseLogLevel
        }

        return logLevel;
    }

    /*
    * Get either testCaseLogLevel or generate it from resultAnnotations (and then remove the annotation)
    * */
    private Collection<ResultAnnotation> findVariableAnnotations() {

        ArrayList<ResultAnnotation> variableAnnotations = new ArrayList<>();

        ResultAnnotation pointer = null;
        for (ResultAnnotation annotation : resultAnnotations) {
            String value = annotation.getAnnotationValue().toString().trim();
            if (value.startsWith("?")) {
                pointer = annotation;
                variableAnnotations.add(annotation);
            }
        }
        if (pointer != null) {
            resultAnnotations.remove(pointer); // remove now that we have testCaseLogLevel
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
}
