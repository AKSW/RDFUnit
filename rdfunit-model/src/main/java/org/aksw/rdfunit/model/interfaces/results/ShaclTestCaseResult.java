package org.aksw.rdfunit.model.interfaces.results;


import org.aksw.rdfunit.model.helper.PropertyValuePair;

import java.util.Set;

public interface ShaclTestCaseResult extends ShaclLiteTestCaseResult {

    /**
     * Collection of ResultAnnotations
     */
    Set<PropertyValuePair> getResultAnnotations();

    /**
     * Collections of dependent TestCaseResults which portray a partial validation result of the test case validated
     * These results are linked to via sh:detail
     */
    Set<TestCaseResult> getDetails();

}
