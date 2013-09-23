package org.aksw.databugger.tests;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 6:31 AM
 */
public class UnitTest {
    public final String sourceUri;
    public final String pattern;
    public final TestAnnotation annotation;
    public final String sparql;
    public final String sparqlPrevalence;


    public UnitTest(String sourceUri, String pattern, TestAnnotation annotation, String sparql, String sparqlPrevalence) {
        this.sourceUri = sourceUri;
        this.pattern = pattern;
        this.annotation = annotation;
        this.sparql = sparql;
        this.sparqlPrevalence = sparqlPrevalence;
    }
}
