package org.aksw.rdfunit.model.impl.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.vocabulary.SHACL;

/**
 * The Simplefied SHACL result that contains only message, severity and focusNode
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM
 * @version $Id: $Id
 */
public class SimpleShaclTestCaseResultImpl extends AbstractTestCaseResultImpl implements SimpleShaclTestCaseResult{

    private final String resource;

    public SimpleShaclTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource) {
        super(testCaseUri, severity, message);
        this.resource = resource;

    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        return super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, SHACL.ValidationResult)
                .addProperty(SHACL.focusNode, model.createResource(getFailingResource()))    //TODO double check later, might not always be the current resource
                .addProperty(SHACL.message, getMessage())
                .addProperty(SHACL.severity, model.createResource(getSeverity().getUri()))
                ;
    }

    public String getFailingResource() {
        return resource;
    }

}
