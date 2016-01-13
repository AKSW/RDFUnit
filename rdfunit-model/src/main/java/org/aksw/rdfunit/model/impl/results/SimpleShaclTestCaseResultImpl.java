package org.aksw.rdfunit.model.impl.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;

/**
 * The Simplefied SHACL result that contains only message, severity and focusNode
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:28 PM
 * @version $Id: $Id
 */
public class SimpleShaclTestCaseResultImpl extends BaseTestCaseResultImpl implements SimpleShaclTestCaseResult{

    private final String resource;

    public SimpleShaclTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource) {
        super(testCaseUri, severity, message);
        this.resource = resource;
    }

    public SimpleShaclTestCaseResultImpl(Resource element, String testCaseUri, RLOGLevel severity, String message, XSDDateTime timestamp, String resource) {
        super(element, testCaseUri, severity, message, timestamp);
        this.resource = resource;
    }

    public String getFailingResource() {
        return resource;
    }

}
