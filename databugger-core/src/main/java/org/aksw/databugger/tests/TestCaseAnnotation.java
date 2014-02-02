package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.enums.TestGenerationType;
import org.aksw.databugger.services.PrefixService;
import org.aksw.databugger.tests.results.ResultAnnotation;

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
        this.resultAnnotations.addAll(resultAnnotations);
        this.testCaseLogLevel = findAnnotationLevel();
        // cache
        this.annotationMessage = findAnnotationMessage();
    }

    public Resource serialize(Resource resource, Model model) {
        resource
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "generated"), model.createResource(getGenerated().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "testGenerator"), model.createResource(getAutoGeneratorURI()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "appliesTo"), model.createResource(getAppliesTo().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(getSourceUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "testCaseLogLevel"), model.createResource(getTestCaseLogLevel()));

        for (String r : getReferences()) {
            resource.addProperty(model.createProperty(PrefixService.getPrefix("tddo") + "references"), ResourceFactory.createResource(r));
        }

        for (ResultAnnotation annotation: resultAnnotations) {
            resource.addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "resultAnnotation"), annotation.serializeAsTestCase(model));
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
        if (resultAnnotations != null) {
            for (ResultAnnotation annotation : resultAnnotations) {
                if (annotation.getAnnotationProperty().equals(PrefixService.getPrefix("rlog")+ "message")) {
                    return annotation.getAnnotationValue().toString();
                }
            }
        }
        return "";
    }

    /*
     * Get either testCAseLogLevel or generate it from resultAnnotations (and then remove the annotation)
     * */
    private String findAnnotationLevel() {

        String logLevel = testCaseLogLevel;


        ResultAnnotation pointer = null;
        for (ResultAnnotation annotation : resultAnnotations) {
            if (annotation.getAnnotationProperty().equals(PrefixService.getPrefix("rlog")+ "level")) {
                pointer = annotation;
            }
        }
        if (pointer != null) {
            if (logLevel == null || logLevel.equals("")) // Get new value only if testCaseLogLevel doesn't exists
                logLevel = pointer.getAnnotationValue().toString();
            resultAnnotations.remove(pointer); // remove now that we have testCaseLogLevel
        }


        return logLevel;
    }

    public String getTestCaseLogLevel() {
        return testCaseLogLevel;
    }
}
