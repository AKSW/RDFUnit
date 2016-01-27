package org.aksw.rdfunit.model.interfaces.results;

import org.aksw.rdfunit.model.helper.PropertyValuePair;

import java.util.Set;




@Deprecated
public interface ExtendedTestCaseResult extends RLOGTestCaseResult {
    Set<PropertyValuePair> getResultAnnotations();
}
