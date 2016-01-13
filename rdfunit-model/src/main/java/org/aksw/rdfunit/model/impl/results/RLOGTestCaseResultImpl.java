package org.aksw.rdfunit.model.impl.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.RLOG;

public class RLOGTestCaseResultImpl extends AbstractTestCaseResultImpl implements RLOGTestCaseResult {

    private final String resource;

    public RLOGTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource) {
        super(testCaseUri, severity, message);
        this.resource = resource;
    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, RDFUNITv.RLOGTestCaseResult)
                .addProperty(RDF.type, RLOG.Entry)
                .addProperty(RLOG.resource, model.createResource(getFailingResource()))
                .addProperty(RLOG.message, getMessage())
                .addProperty(RLOG.level, model.createResource(getSeverity().getUri()))
                ;
    }

    public String getFailingResource() {
        return resource;
    }
}
