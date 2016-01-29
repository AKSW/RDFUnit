package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import lombok.*;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;

/**
 * ManualTestCase Implementation
 *
 * @author Dimitris Kontokostas
 * @since 1/3/14 3:57 PM
 * @version $Id: $Id
 */
@Value
@EqualsAndHashCode(callSuper=true)
public class ManualTestCaseImpl extends AbstractTestCaseImpl implements TestCase {
    @Getter @NonNull private final String sparqlWhere;
    @Getter @NonNull private final String sparqlPrevalence;

    @Builder
    private ManualTestCaseImpl(Resource resource, TestCaseAnnotation annotation, String sparqlWhere, String sparqlPrevalence) {
        super(resource, annotation);
        this.sparqlWhere = sparqlWhere;
        this.sparqlPrevalence = sparqlPrevalence;
    }

}
