package org.aksw.rdfunit.model.interfaces.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.impl.results.TestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;


/**
 * @author Dimitris Kontokostas
 * @since 1 /6/14 3:26 PM
 * @version $Id: $Id
 */
public interface StatusTestCaseResult extends TestCaseResult {
    TestCaseResultStatus getStatus();

}
