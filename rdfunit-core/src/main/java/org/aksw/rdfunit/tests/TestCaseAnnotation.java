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
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:40 PM
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
    }

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

    public RLOGLevel getTestCaseLogLevel() {
        return testCaseLogLevel;
    }

    public Collection<ResultAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }

    public String getDescription() {
        return description;
    }
}
