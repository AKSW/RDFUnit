package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.services.PrefixService;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 3:57 PM
 */
public class ManualTestCase extends TestCase {
    protected final String sparqlWhere;
    protected final String sparqlPrevalence;

    public ManualTestCase(String testURI, TestCaseAnnotation annotation, String sparqlWhere, String sparqlPrevalence) {
        super(testURI, annotation);
        this.sparqlWhere = sparqlWhere;
        this.sparqlPrevalence = sparqlPrevalence;
    }

    @Override
    public Resource serialize(Model model) {

        Resource resource = super.serialize(model);

        resource
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "ManualTestCase"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "sparqlWhere"), getSparqlWhere())
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "sparqlPrevalence"), getSparqlPrevalence());

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
