package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.enums.TestGenerationType;
import org.aksw.databugger.services.PrefixService;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 3:40 PM
 */
public class TestCaseAnnotation {
    private final TestGenerationType generated;
    private final String autoGeneratorURI;
    private final TestAppliesTo appliesTo;
    private final String sourceUri;
    private final List<String> references;

    public TestCaseAnnotation(TestGenerationType generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, List<String> references) {
        this.generated = generated;
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = appliesTo;
        this.sourceUri = sourceUri;
        this.references = references;
    }

    public Resource saveAnnotationToResource(Resource resource, Model model) {
        resource
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "generated"), model.createResource(getGenerated().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "testGenerator"), model.createResource(getAutoGeneratorURI()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "appliesTo"), model.createResource(getAppliesTo().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(getSourceUri()));

        for (String r : getReferences()) {
            resource.addProperty(model.createProperty(PrefixService.getPrefix("tddo") + "references"), ResourceFactory.createResource(r));
        }

        return resource;
    }

    public TestGenerationType getGenerated() {
        return generated;
    }

    public String getAutoGeneratorURI() {
        return autoGeneratorURI;
    }

    public TestAppliesTo getAppliesTo() {
        return appliesTo;
    }

    public String getSourceUri() {
        return sourceUri;
    }

    public List<String> getReferences() {
        return references;
    }
}
