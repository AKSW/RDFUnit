package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.model.impl.ResultAnnotationImpl;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.Collections;
import java.util.Set;

public interface TestCaseGroup extends GenericTestCase {

    Set<GenericTestCase> getTestCases();

    SHACL.LogicalConstraint getLogicalOperator();

    static void addTestCaseGroupResultAnnotation(Resource testCase, Property group, Resource groupUri, TestCaseAnnotation tca){
        //TODO find annotation property
        ResultAnnotation ra = new ResultAnnotationImpl.Builder(testCase, group).setValue(groupUri).build();
        tca.addResultAnnotations(Collections.singleton(ra));
    }
}
