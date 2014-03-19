package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.services.PrefixService;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 3:40 PM
 */
public class TestCaseAnnotation {
    private final TestGenerationType generated;
    private final String autoGeneratorURI;
    private final TestAppliesTo appliesTo;
    private final String sourceUri;
    private final List<String> references;
    private final String testCaseLogLevel;
    private final List<ResultAnnotation> resultAnnotations;
    //cache for access
    private final String annotationMessage;


    public TestCaseAnnotation(TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, List<String> references, String testCaseLogLevel, List<ResultAnnotation> resultAnnotations) {
        this.generated = generated;
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = appliesTo;
        this.sourceUri = sourceUri;
        this.references = references;
        this.resultAnnotations = new ArrayList<ResultAnnotation>();
        this.getResultAnnotations().addAll(resultAnnotations);
        this.testCaseLogLevel = findAnnotationLevel(testCaseLogLevel);
        // cache
        this.annotationMessage = findAnnotationMessage();
    }

    public Resource serialize(Resource resource, Model model) {
        resource
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "generated"), model.createResource(getGenerated().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "testGenerator"), model.createResource(getAutoGeneratorURI()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "appliesTo"), model.createResource(getAppliesTo().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "source"), model.createResource(getSourceUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "testCaseLogLevel"), model.createResource(getTestCaseLogLevel()));

        for (String r : getReferences()) {
            resource.addProperty(model.createProperty(PrefixService.getPrefix("ruto") + "references"), ResourceFactory.createResource(r));
        }

        for (ResultAnnotation annotation : getResultAnnotations()) {
            resource.addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "resultAnnotation"), annotation.serializeAsTestCase(model));
        }

        return resource;
    }

    public TestGenerationType getGenerated() {
        return generated;
    }

    public String getAutoGeneratorURI() {
        return autoGeneratorURI;
    }

    public TestAppliesTo getAppliesTo() {
        return appliesTo;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public List<String> getReferences() {
        return references;
    }

    public String getAnnotationMessage() {
        return annotationMessage;
    }

    private String findAnnotationMessage() {
        if (getResultAnnotations() != null) {
            for (ResultAnnotation annotation : getResultAnnotations()) {
                if (annotation.getAnnotationProperty().equals(PrefixService.getPrefix("rlog") + "message")) {
                    return annotation.getAnnotationValue().toString();
                }
            }
        }
        return "";
    }

    /*
     * Get either testCAseLogLevel or generate it from resultAnnotations (and then remove the annotation)
     * */
    private String findAnnotationLevel(String testCaseLogLevel) {

        String logLevel = testCaseLogLevel;


        ResultAnnotation pointer = null;
        for (ResultAnnotation annotation : getResultAnnotations()) {
            if (annotation.getAnnotationProperty().equals(PrefixService.getPrefix("rlog") + "level")) {
                pointer = annotation;
            }
        }
        if (pointer != null) {
            if (logLevel == null || logLevel.equals("")) // Get new value only if testCaseLogLevel doesn't exists
                logLevel = pointer.getAnnotationValue().toString();
            getResultAnnotations().remove(pointer); // remove now that we have testCaseLogLevel
        }


        return logLevel;
    }

    public String getTestCaseLogLevel() {
        return testCaseLogLevel;
    }

    public List<ResultAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }
}
