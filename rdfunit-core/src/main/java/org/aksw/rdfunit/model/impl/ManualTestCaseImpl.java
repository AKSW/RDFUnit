package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

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
     * <p>Constructor for ManualTestCase.</p>
     *
     * @param testURI a {@link java.lang.String} object.
     * @param annotation a {@link TestCaseAnnotation} object.
     * @param sparqlWhere a {@link java.lang.String} object.
     * @param sparqlPrevalence a {@link java.lang.String} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public ManualTestCaseImpl(String testURI, TestCaseAnnotation annotation, String sparqlWhere, String sparqlPrevalence) {
        super(testURI, annotation);
        this.sparqlWhere = sparqlWhere.trim();
        this.sparqlPrevalence = sparqlPrevalence.trim();
    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model) {

        Resource resource = super.serialize(model);

        resource
                .addProperty(RDF.type, RDFUNITv.ManualTestCase)
                .addProperty(RDFUNITv.sparqlWhere, getSparqlWhere())
                .addProperty(RDFUNITv.sparqlPrevalence, getSparqlPrevalence());

        return resource;
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
