package org.aksw.rdfunit.model.readers.results;

import org.aksw.rdfunit.model.impl.results.SimpleShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.SHACL;
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
public final class ShaclSimpleTestCaseResultReader implements ElementReader<SimpleShaclTestCaseResult> {

    private ShaclSimpleTestCaseResultReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link ShaclSimpleTestCaseResultReader} object.
     */
    public static ShaclSimpleTestCaseResultReader create() { return new ShaclSimpleTestCaseResultReader();}

    /** {@inheritDoc} */
    @Override
    public SimpleShaclTestCaseResult read(final Resource resource) {
        checkNotNull(resource);

        TestCaseResult test = TestCaseResultReader.create(SHACL.message, SHACL.severity).read(resource);

        String focusNode = null;
        for (Statement smt : resource.listProperties(SHACL.focusNode).toList()) {
            focusNode = smt.getObject().asResource().getURI();
        }
        checkNotNull(focusNode);

        return new SimpleShaclTestCaseResultImpl(resource, test.getTestCaseUri(), test.getSeverity(), test.getMessage(), test.getTimestamp(), focusNode);
    }
}
