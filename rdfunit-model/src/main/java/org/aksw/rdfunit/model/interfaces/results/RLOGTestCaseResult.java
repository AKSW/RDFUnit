package org.aksw.rdfunit.model.interfaces.results;


import org.aksw.rdfunit.enums.RLOGLevel;


public interface RLOGTestCaseResult extends TestCaseResult {

    String getFailingResource();

}
