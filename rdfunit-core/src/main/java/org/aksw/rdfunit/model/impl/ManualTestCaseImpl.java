package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;

/**
 * <p>ManualTestCase class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:57 PM
 * @version $Id: $Id
 */
public class ManualTestCaseImpl extends AbstractTestCaseImpl implements TestCase {
    private final String sparqlWhere;
    private final String sparqlPrevalence;

    public ManualTestCaseImpl(Resource tcResource, TestCaseAnnotation annotation, String sparqlWhere, String sparqlPrevalence) {
        super(tcResource, annotation);
        this.sparqlWhere = sparqlWhere;
        this.sparqlPrevalence = sparqlPrevalence;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlWhere() {
        return sparqlWhere;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlPrevalence() {
        return sparqlPrevalence;
    }
}
