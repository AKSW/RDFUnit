package org.aksw.rdfunit.model.interfaces.results;


import org.aksw.rdfunit.model.helper.SimpleAnnotation;

import java.util.Set;

public interface ShaclTestCaseResult extends ExtendedTestCaseResult {
    Set<SimpleAnnotation> getResultAnnotations();

}
