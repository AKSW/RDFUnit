package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.rdfunit.Utils.DatabuggerUtils;
import org.aksw.rdfunit.exceptions.TestCaseException;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.services.PrefixService;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 3:49 PM
 */
public class PatternBasedTestCase extends TestCase {

    private final Pattern pattern;
    private final List<Binding> bindings;

    public PatternBasedTestCase(String testURI, TestCaseAnnotation annotation, Pattern pattern, List<Binding> bindings) throws TestCaseException {
        super(testURI, annotation);
        this.pattern = pattern;
        this.bindings = bindings;

        // validate
        if (bindings.size() != pattern.getParameters().size()) {
            throw new TestCaseException("Non valid bindings in TestCase: " + testURI);
        }
        validateQueries();
    }

    @Override
    public Resource serialize(Model model) {

        Resource resource = super.serialize(model);

        resource
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("ruto") + "PatternBasedTestCase"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "basedOnPattern"), model.createResource(PrefixService.getPrefix("rutp") + pattern.getId()))
                .addProperty(RDFS.comment, "SPARQL Query: \n" + DatabuggerUtils.getAllPrefixes() + getSparql() + "\n Pprevalence SPARQL Query :\n" + getSparqlPrevalence());


        for (Binding binding : bindings) {
            resource.addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("ruto"), "binding"), binding.writeToModel(model));
        }

        return resource;
    }

    @Override
    public String getSparqlWhere() {
        return instantiateBindings(bindings, pattern.getSparqlWherePattern());
    }

    @Override
    public String getSparqlPrevalence() {
        return instantiateBindings(bindings, pattern.getSparqlPatternPrevalence());
    }

    private String instantiateBindings(List<Binding> bindings, String query) {
        String sparql = query;
        for (Binding b : bindings) {
            sparql = sparql.replace("%%" + b.getParameterId() + "%%", b.getValue());
        }
        return sparql;
    }
}
