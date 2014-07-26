package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.services.PrefixNSService;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:57 PM
 */
public class ManualTestCase extends TestCase {
    private final String sparqlWhere;
    private final String sparqlPrevalence;

    public ManualTestCase(String testURI, TestCaseAnnotation annotation, String sparqlWhere, String sparqlPrevalence) throws TestCaseInstantiationException {
        super(testURI, annotation);
        this.sparqlWhere = sparqlWhere.trim();
        this.sparqlPrevalence = sparqlPrevalence.trim();
        validateQueries();
    }

    @Override
    public Resource serialize(Model model) {

        Resource resource = super.serialize(model);

        resource
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:ManualTestCase")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:sparqlWhere")), getSparqlWhere())
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:sparqlPrevalence")), getSparqlPrevalence());

        return resource;
    }

    @Override
    public String getSparqlWhere() {
        return sparqlWhere;
    }

    @Override
    public String getSparqlPrevalence() {
        return sparqlPrevalence;
    }
}
