package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.services.PrefixService;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/6/14 8:33 AM
 */
public class TestSuite {
    private final java.util.Collection<TestCase> testCases;

    public TestSuite(java.util.Collection<TestCase> testCases) {
        this.testCases = testCases;
    }

    public java.util.Collection<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(java.util.Collection<TestCase> testCases) {
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    public int size() {
        return testCases.size();
    }

    public Resource serialize(Model model) {
        Resource resource = model.createResource(JenaUUID.generate().asString())
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("rut") + "TestSuite"))
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("prov") + "Collection"));

        for (TestCase tc : testCases) {
            resource.addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("prov"), "hadMember"), model.createResource(tc.getTestURI()));
        }
        // TODO check whether to dump the complete test

        return resource;
    }
}
