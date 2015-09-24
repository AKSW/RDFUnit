package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;

import java.util.Collection;

/**
 * <p>TestSuite class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/6/14 8:33 AM
 * @version $Id: $Id
 */
public class TestSuite {
    private final Collection<TestCase> testCases;

    /**
     * <p>Constructor for TestSuite.</p>
     *
     * @param testCases a {@link java.util.Collection} object.
     */
    public TestSuite(Collection<TestCase> testCases) {
        this.testCases = testCases;
    }

    /**
     * <p>Getter for the field <code>testCases</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<TestCase> getTestCases() {
        return testCases;
    }

    /**
     * <p>Setter for the field <code>testCases</code>.</p>
     *
     * @param testCases a {@link java.util.Collection} object.
     */
    public void setTestCases(Collection<TestCase> testCases) {
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    /**
     * <p>size.</p>
     *
     * @return a int.
     */
    public int size() {
        return testCases.size();
    }

    /**
     * <p>serialize.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     */
    public Resource serialize(Model model) {
        Resource resource = model.createResource(PrefixNSService.getURIFromAbbrev("ruts:" + JenaUUID.generate().asString()))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:TestSuite")))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("prov:Collection")));

        for (TestCase tc : testCases) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:hadMember")), model.createResource(tc.getTestURI()));
        }
        // TODO check whether to dump the complete test

        return resource;
    }
}
