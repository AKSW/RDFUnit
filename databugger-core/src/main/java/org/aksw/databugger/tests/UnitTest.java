package org.aksw.databugger.tests;

import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.enums.TestGeneration;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 6:31 AM
 */
public class UnitTest {
    public final String pattern;
    public final TestGeneration generated;
    public final String autoGeneratorURI;
    public final TestAppliesTo appliesTo;
    public final String sourceUri;
    public final TestAnnotation annotation;
    public final String sparql;
    public final String sparqlPrevalence;

    public UnitTest(String sparql, String sparqlPrevalence) {
        this("",TestGeneration.ManuallyGenerated,"",null,"",null,sparql,sparqlPrevalence);
    }

    public UnitTest(String pattern, TestGeneration generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, TestAnnotation annotation, String sparql, String sparqlPrevalence) {
        this.pattern = pattern;
        this.generated = generated;
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = appliesTo;
        this.sourceUri = sourceUri;
        this.annotation = annotation;
        this.sparql = sparql;
        this.sparqlPrevalence = sparqlPrevalence;
    }
}
