package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.impl.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class ManualTestCaseReader implements ElementReader<TestCase> {

    private ManualTestCaseReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link ManualTestCaseReader} object.
     */
    public static ManualTestCaseReader create() { return new ManualTestCaseReader();}


    /** {@inheritDoc} */
    @Override
    public TestCase read(Resource resource) {
        checkNotNull(resource, "Cannot read a ManualTestCase from a null resource");


        String sparqlWhere = null;
        String sparqlPrevalence = null;

        int count = 0; // used to count duplicates

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlWhere).toList()) {
            checkArgument(count++ == 0, "Cannot have more than one rut:sparqlWhere in Test %s", resource.getURI());
            sparqlWhere = smt.getObject().asLiteral().getLexicalForm();
        }

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.sparqlPrevalence).toList()) {
            checkArgument(count++ == 0, "Cannot have more than one rut:sparqlPrevalence in Test %s", resource.getURI());
            sparqlPrevalence = smt.getObject().asLiteral().getLexicalForm();
        }

        TestCaseAnnotation annotation = TestCaseAnnotationReader.create().read(resource);

        return new ManualTestCaseImpl(resource.getURI(), annotation, sparqlWhere, sparqlPrevalence);
    }
}
