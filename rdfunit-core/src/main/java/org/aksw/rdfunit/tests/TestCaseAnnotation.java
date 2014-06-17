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
    private final java.util.Collection<String> references;
    private final String description;
    private final RLOGLevel testCaseLogLevel;
    private final java.util.Collection<ResultAnnotation> resultAnnotations;

    public TestCaseAnnotation(TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, java.util.Collection<String> references, String description, RLOGLevel testCaseLogLevel, java.util.Collection<ResultAnnotation> resultAnnotations) {
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
    }

    public Resource serialize(Resource resource, Model model) {
        resource
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("dcterms"), "description"), description)
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "generated"), model.createResource(getGenerated().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "testGenerator"), model.createResource(getAutoGeneratorURI()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "appliesTo"), model.createResource(getAppliesTo().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "source"), model.createResource(getSourceUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "testCaseLogLevel"), model.createResource(getTestCaseLogLevel().getUri()));

        for (String r : getReferences()) {
            resource.addProperty(model.createProperty(PrefixNSService.getNSFromPrefix("rut") + "references"), ResourceFactory.createResource(r));
        }

        for (ResultAnnotation annotation : resultAnnotations) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getNSFromPrefix("rut"), "resultAnnotation"), annotation.serializeAsTestCase(model));
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

    public java.util.Collection<String> getReferences() {
        return references;
    }


    /*
     * Get either testCaseLogLevel or generate it from resultAnnotations (and then remove the annotation)
     * */
    private RLOGLevel findAnnotationLevel(RLOGLevel testCaseLogLevel) {

        RLOGLevel logLevel = testCaseLogLevel;

        ResultAnnotation pointer = null;
        for (ResultAnnotation annotation : resultAnnotations) {
            if (annotation.getAnnotationProperty().equals(PrefixNSService.getNSFromPrefix("rlog") + "level")) {
                pointer = annotation;
            }
        }
        if (pointer != null) {
            if (logLevel == null) {// Get new value only if testCaseLogLevel doesn't exists
                logLevel = RLOGLevel.resolve(pointer.getAnnotationValue().toString());
            }
            resultAnnotations.remove(pointer); // remove now that we have testCaseLogLevel
        }

        if (logLevel == null) {
            String ff = "";
        }

        return logLevel;
    }

    public RLOGLevel getTestCaseLogLevel() {
        return testCaseLogLevel;
    }

    public java.util.Collection<ResultAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }

    public String getDescription() {
        return description;
    }
}
