package org.aksw.rdfunit.model.readers.results;

import org.aksw.rdfunit.model.impl.results.RLOGTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.RLOG;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class RLOGTestCaseResultReader implements ElementReader<RLOGTestCaseResult> {

    private RLOGTestCaseResultReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link RLOGTestCaseResultReader} object.
     */
    public static RLOGTestCaseResultReader create() { return new RLOGTestCaseResultReader();}

    /** {@inheritDoc} */
    @Override
    public RLOGTestCaseResult read(final Resource resource) {
        checkNotNull(resource);

        TestCaseResult test = TestCaseResultReader.create(RLOG.message, RLOG.level).read(resource);

        String res = null;
        for (Statement smt : resource.listProperties(RLOG.resource).toList()) {
            res = smt.getObject().asResource().getURI();
        }
        checkNotNull(res);

        return new RLOGTestCaseResultImpl(resource, test.getTestCaseUri(), test.getSeverity(), test.getMessage(), test.getTimestamp(), res);
    }
}
