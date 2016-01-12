package org.aksw.rdfunit.model.interfaces.results;

import com.google.common.base.Optional;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.Calendar;

/**
 * An abstract Test Case Result.
 *
 * @author Dimitris Kontokostas
 * @since 1 /2/14 3:44 PM
 * @version $Id: $Id
 */
public interface TestCaseResult {

    Optional<TestCase> getTestCase();
    String getTestCaseUri();
    XSDDateTime getTimestamp();

    Resource serialize(Model model, String testExecutionURI);

}