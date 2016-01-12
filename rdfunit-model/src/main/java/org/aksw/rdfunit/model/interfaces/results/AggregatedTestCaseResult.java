package org.aksw.rdfunit.model.interfaces.results;

import com.google.common.base.Optional;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.TestCaseResultStatus;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

public interface AggregatedTestCaseResult extends StatusTestCaseResult {

    long getErrorCount();

    Optional<Long> getPrevalenceCount();
}
