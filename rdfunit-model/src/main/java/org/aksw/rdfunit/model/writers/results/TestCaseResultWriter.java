package org.aksw.rdfunit.model.writers.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.model.writers.ElementWriterUtils;
import org.aksw.rdfunit.vocabulary.PROV;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import static com.google.common.base.Preconditions.checkNotNull;


public final class TestCaseResultWriter implements ElementWriter {

    private final TestCaseResult testCaseResult;
    private final String uriPrefix;

    private TestCaseResultWriter(TestCaseResult testCaseResult, String uriPrefix) {
        this.testCaseResult = checkNotNull(testCaseResult);
        checkNotNull(uriPrefix);
        if (uriPrefix.isEmpty()) {
            this.uriPrefix = "";
        } else {
            this.uriPrefix = uriPrefix + "/";
        }

    }

    public static TestCaseResultWriter create(TestCaseResult testCaseResult, String uriPrefix) {return new TestCaseResultWriter(testCaseResult, uriPrefix);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = null;
        if (uriPrefix.isEmpty()) {
            resource = ElementWriterUtils.copyElementResourceInModel(testCaseResult, model);
        } else {
            resource = model.createResource(uriPrefix + testCaseResult.getMessage());
        }


        //Resource testSuiteResource = testSuite.serialize(getModel());



        return resource;
    }
}
