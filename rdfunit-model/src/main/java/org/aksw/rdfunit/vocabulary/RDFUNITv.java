package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Core RDFUnit Vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 * @version $Id: $Id
 */
@SuppressWarnings("ClassWithTooManyFields")
public final class RDFUNITv {

    //namespace
    /** Constant <code>namespace="http://rdfunit.aksw.org/ns/core#"</code> */
    public static final String namespace = "http://rdfunit.aksw.org/ns/core#";

    //Classes
    /** Constant <code>Binding</code> */
    public static final Resource Binding = resource("Binding");
    /** Constant <code>Pattern</code> */
    public static final Resource Pattern = resource("Pattern");
    /** Constant <code>ResultAnnotation</code> */
    public static final Resource ResultAnnotation = resource("ResultAnnotation");
    /** Constant <code>RLOGTestCaseResult</code> */
    public static final Resource RLOGTestCaseResult = resource("RLOGTestCaseResult");
    /** Constant <code>TestGenerator</code> */
    public static final Resource TestGenerator = resource("TestGenerator");
    /** Constant <code>Parameter</code> */
    public static final Resource Parameter = resource("Parameter");
    /** Constant <code>PatternBasedTestCase</code> */
    public static final Resource PatternBasedTestCase = resource("PatternBasedTestCase");
    /** Constant <code>ManualTestCase</code> */
    public static final Resource ManualTestCase = resource("ManualTestCase");
    /** Constant <code>StatusTestCaseResult</code> */
    public static final Resource StatusTestCaseResult = resource("StatusTestCaseResult");
    /** Constant <code>TestCaseResult</code> */
    public static final Resource TestCaseResult = resource("TestCaseResult");
    /** Constant <code>AggregatedTestResult</code> */
    public static final Resource AggregatedTestResult = resource("AggregatedTestResult");
    /** Constant <code>ExtendedTestCaseResult</code> */
    public static final Resource ExtendedTestCaseResult = resource("ExtendedTestCaseResult");
    public static final Resource TestExecution = resource("TestExecution");
    public static final Resource TestSuite = resource("TestSuite");




    //properties
    /** Constant <code>binding</code> */
    public static final Property binding = property("binding");
    /** Constant <code>bindingValue</code> */
    public static final Property bindingValue = property("bindingValue");
    /** Constant <code>parameter</code> */
    public static final Property parameter = property("parameter");
    /** Constant <code>annotationProperty</code> */
    public static final Property annotationProperty = property("annotationProperty");
    /** Constant <code>annotationValue</code> */
    public static final Property annotationValue = property("annotationValue");
    /** Constant <code>resultAnnotation</code> */
    public static final Property resultAnnotation = property("resultAnnotation");
    /** Constant <code>sparqlGenerator</code> */
    public static final Property sparqlGenerator = property("sparqlGenerator");
    /** Constant <code>basedOnPattern</code> */
    public static final Property basedOnPattern = property("basedOnPattern");
    /** Constant <code>parameterConstraint</code> */
    public static final Property parameterConstraint = property("parameterConstraint");
    /** Constant <code>constraintPattern</code> */
    public static final Property constraintPattern = property("constraintPattern");
    /** Constant <code>sparqlWherePattern</code> */
    public static final Property sparqlWherePattern = property("sparqlWherePattern");
    /** Constant <code>sparqlPrevalencePattern</code> */
    public static final Property sparqlPrevalencePattern = property("sparqlPrevalencePattern");
    /** Constant <code>sparqlWhere</code> */
    public static final Property sparqlWhere = property("sparqlWhere");
    /** Constant <code>sparqlPrevalence</code> */
    public static final Property sparqlPrevalence = property("sparqlPrevalence");

    /** Constant <code>appliesTo</code> */
    public static final Property appliesTo = property("appliesTo");
    /** Constant <code>generated</code> */
    public static final Property generated = property("generated");
    /** Constant <code>source</code> */
    public static final Property source = property("source");
    /** Constant <code>testCaseLogLevel</code> */
    public static final Property testCaseLogLevel = property("testCaseLogLevel");
    /** Constant <code>testGenerator</code> */
    public static final Property testGenerator = property("testGenerator");
    /** Constant <code>references</code> */
    public static final Property references = property("references");

    /** Constant <code>resultStatus</code> */
    public static final Property resultStatus = property("resultStatus");

    /** Constant <code>testCase</code> */
    public static final Property testCase = property("testCase");

    /** Constant <code>resultCount</code> */
    public static final Property resultCount = property("resultCount");
    /** Constant <code>resultPrevalence</code> */
    public static final Property resultPrevalence = property("resultPrevalence");

    public static final Property testsRun = property("testsRun");
    public static final Property testsSuceedded = property("testsSuceedded");
    public static final Property testsFailed = property("testsFailed");
    public static final Property testsTimeout = property("testsTimeout");
    public static final Property testsError = property("testsError");
    public static final Property totalIndividualErrors = property("totalIndividualErrors");
    public static final Property executionType = property("executionType");

    public static final Property metric = property("metric");



    private RDFUNITv() {

    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }
}
