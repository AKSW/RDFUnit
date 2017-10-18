package org.aksw.rdfunit.model.interfaces.results;


import org.aksw.rdfunit.model.helper.PropertyValuePair;

import java.util.Set;

public interface ShaclTestCaseResult extends ShaclLiteTestCaseResult {
    Set<PropertyValuePair> getResultAnnotations();

}
