package org.aksw.rdfunit.model.impl.results;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.SimpleAnnotation;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;


import java.util.*;



public class ShaclTestCaseResultImpl extends ExtendedTestCaseResultImpl implements ShaclTestCaseResult{

    public ShaclTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource, Set<SimpleAnnotation> resultAnnotations) {
        super(testCaseUri, severity, message, resource, resultAnnotations);
    }
    /*
    public Resource serialize(Model model, String testExecutionURI) {
        Resource resource = super.serialize(model, testExecutionURI)
            .addProperty(SHACL.subject, model.createResource(getResource()));


        for (Map.Entry<ResultAnnotation, Set<RDFNode>> vaEntry : variableAnnotationsMap.entrySet()) {
            for (RDFNode rdfNode : vaEntry.getValue()) {
                resource.addProperty(vaEntry.getKey().getAnnotationProperty(), rdfNode);
            }
        }

        return resource;
    }
      */
}
