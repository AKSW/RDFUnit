package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixNSService;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/6/14 8:33 AM
 */
public class TestSuite {
    private final Collection<TestCase> testCases;

    public TestSuite(Collection<TestCase> testCases) {
        this.testCases = testCases;
    }

    public Collection<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(Collection<TestCase> testCases) {
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    public int size() {
        return testCases.size();
    }

    public Resource serialize(Model model) {
        Resource resource = model.createResource(JenaUUID.generate().asString())
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:TestSuite")))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("prov:Collection")));

        for (TestCase tc : testCases) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("prov:hadMember")), model.createResource(tc.getTestURI()));
        }
        // TODO check whether to dump the complete test

        return resource;
    }
}
