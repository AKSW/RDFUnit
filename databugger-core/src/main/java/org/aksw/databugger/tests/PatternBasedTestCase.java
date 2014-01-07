package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.services.PrefixService;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/3/14 3:49 PM
 */
public class PatternBasedTestCase extends TestCase {

    private final Pattern pattern;
    private final List<Binding> bindings;

    public PatternBasedTestCase(String testURI, TestCaseAnnotation annotation, Pattern pattern, List<Binding> bindings) {
        super(testURI, annotation);
        this.pattern = pattern;
        this.bindings = bindings;
    }

    @Override
    public Resource serialize(Model model) {

        Resource resource = super.serialize(model);

        resource
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "PatternBasedTestCase"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "basedOnPattern"), model.createResource(PrefixService.getPrefix("tddp") + pattern.getId()))
                .addProperty(RDFS.comment, "SPARQL Query: \n" + DatabuggerUtils.getAllPrefixes() + getSparql() + "\n Pprevalence SPARQL Query :\n" + getSparqlPrevalence());


        for (Binding binding : bindings) {
            resource.addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "binding"), binding.writeToModel(model));
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
