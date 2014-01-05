package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
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
    private final String message;
    private final String logLevel;

    public PatternBasedTestCase(String testURI, TestCaseAnnotation annotation, Pattern pattern, List<Binding> bindings, String message, String logLevel) {
        super(testURI, annotation);
        this.pattern = pattern;
        this.bindings = bindings;
        this.message = message;
        this.logLevel = logLevel;
    }

    @Override
    public Resource saveTestToModel(Model model) {

        Resource resource = super.saveTestToModel(model);

        resource
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "PatternBasedTestCase"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "basedOnPattern"), model.createResource(PrefixService.getPrefix("tddp") + pattern.getId()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rlog"), "message"), message)
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("rlog"), "level"), model.createResource(logLevel));


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
        for (int i = 0; i < bindings.size(); i++) {
            sparql = sparql.replace("%%" + pattern.getParameters().get(i).getId() + "%%", bindings.get(i).getValue());
        }
        return sparql;
    }
}
