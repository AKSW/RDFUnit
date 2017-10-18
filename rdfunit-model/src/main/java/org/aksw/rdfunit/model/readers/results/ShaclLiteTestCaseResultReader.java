package org.aksw.rdfunit.model.readers.results;

import org.aksw.rdfunit.model.impl.results.ShaclLiteTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.results.ShaclLiteTestCaseResult;
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
public final class ShaclLiteTestCaseResultReader implements ElementReader<ShaclLiteTestCaseResult> {

    private ShaclLiteTestCaseResultReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link ShaclLiteTestCaseResultReader} object.
     */
    public static ShaclLiteTestCaseResultReader create() { return new ShaclLiteTestCaseResultReader();}

    /** {@inheritDoc} */
    @Override
    public ShaclLiteTestCaseResult read(final Resource resource) {
        checkNotNull(resource);

        TestCaseResult test = TestCaseResultReader.create(SHACL.message, SHACL.severity).read(resource);

        String focusNode = null;
        for (Statement smt : resource.listProperties(SHACL.focusNode).toList()) {
            focusNode = smt.getObject().asResource().getURI();
        }
        checkNotNull(focusNode);

        return new ShaclLiteTestCaseResultImpl(resource, test.getTestCaseUri(), test.getSeverity(), test.getMessage(), test.getTimestamp(), focusNode);
    }
}
