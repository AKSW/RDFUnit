package org.aksw.rdfunit.model.impl;

import lombok.*;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.apache.jena.rdf.model.Resource;

/**
 * ManualTestCase Implementation
 *
 * @author Dimitris Kontokostas
 * @since 1/3/14 3:57 PM
 * @version $Id: $Id
 */
@Value
@EqualsAndHashCode
public class ManualTestCaseImpl implements TestCase {
    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final TestCaseAnnotation testCaseAnnotation;

    @Getter @NonNull private final String sparqlWhere;
    @Getter @NonNull private final String sparqlPrevalence;

    @Builder
    private ManualTestCaseImpl(Resource resource, TestCaseAnnotation annotation, String sparqlWhere, String sparqlPrevalence) {
        this.element = resource;
        this.testCaseAnnotation = annotation;
        this.sparqlWhere = sparqlWhere;
        this.sparqlPrevalence = sparqlPrevalence;
    }

}
