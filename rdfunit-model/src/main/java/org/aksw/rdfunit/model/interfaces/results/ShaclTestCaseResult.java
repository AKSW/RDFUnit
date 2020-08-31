package org.aksw.rdfunit.model.interfaces.results;


import java.util.Set;
import org.aksw.rdfunit.model.helper.PropertyValuePair;

public interface ShaclTestCaseResult extends ShaclLiteTestCaseResult {

  Set<PropertyValuePair> getResultAnnotations();

}
