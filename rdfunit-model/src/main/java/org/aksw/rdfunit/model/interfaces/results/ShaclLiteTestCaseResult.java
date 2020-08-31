package org.aksw.rdfunit.model.interfaces.results;


import org.apache.jena.rdf.model.RDFNode;

public interface ShaclLiteTestCaseResult extends TestCaseResult {

  RDFNode getFailingNode();
}
