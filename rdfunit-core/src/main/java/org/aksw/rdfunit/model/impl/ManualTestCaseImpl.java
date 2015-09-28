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

    /**
     * <p>Constructor for ManualTestCaseImpl.</p>
     *
     * @param tcResource a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param annotation a {@link org.aksw.rdfunit.model.interfaces.TestCaseAnnotation} object.
     * @param sparqlWhere a {@link java.lang.String} object.
     * @param sparqlPrevalence a {@link java.lang.String} object.
     */
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
