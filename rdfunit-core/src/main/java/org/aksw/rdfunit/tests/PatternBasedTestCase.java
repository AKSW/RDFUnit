package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.services.PrefixNSService;

import java.util.Collection;

/**
 * <p>PatternBasedTestCase class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/3/14 3:49 PM
 * @version $Id: $Id
 */
public class PatternBasedTestCase extends TestCase {

    private final Pattern pattern;
    private final Collection<Binding> bindings;
    private String sparqlWhereCache = null;
    private String sparqlPrevalenceCache = null;

    /**
     * <p>Constructor for PatternBasedTestCase.</p>
     *
     * @param testURI a {@link java.lang.String} object.
     * @param annotation a {@link org.aksw.rdfunit.tests.TestCaseAnnotation} object.
     * @param pattern a {@link org.aksw.rdfunit.patterns.Pattern} object.
     * @param bindings a {@link java.util.Collection} object.
     * @throws org.aksw.rdfunit.exceptions.TestCaseInstantiationException if any.
     */
    public PatternBasedTestCase(String testURI, TestCaseAnnotation annotation, Pattern pattern, Collection<Binding> bindings) throws TestCaseInstantiationException {
        super(testURI, annotation);
        this.pattern = pattern;
        this.bindings = bindings;

        // validate
        if (bindings.size() != pattern.getParameters().size()) {
            throw new TestCaseInstantiationException("Non valid bindings in TestCase: " + testURI);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model) {

        Resource resource = super.serialize(model);

        resource
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:PatternBasedTestCase")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:basedOnPattern")), model.createResource(PrefixNSService.getURIFromAbbrev("rutp:" + pattern.getId())))
                .addProperty(RDFS.comment, "FOR DEBUGGING ONLY: SPARQL Query: \n" + new QueryGenerationSelectFactory().getSparqlQueryAsString(this) + "\n Prevalence SPARQL Query :\n" + getSparqlPrevalence());


        for (Binding binding : bindings) {
            resource.addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:binding")), binding.writeToModel(model));
        }

        return resource;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlWhere() {
        if (sparqlWhereCache == null) {
            sparqlWhereCache = instantiateBindings(bindings, pattern.getSparqlWherePattern()).trim();
        }
        return sparqlWhereCache;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlPrevalence() {
        if (sparqlPrevalenceCache == null) {
            sparqlPrevalenceCache = instantiateBindings(bindings, pattern.getSparqlPatternPrevalence()).trim();
        }
        return sparqlPrevalenceCache;
    }

    private String instantiateBindings(Collection<Binding> bindings, String query) {
        String sparql = query;
        for (Binding b : bindings) {
            sparql = sparql.replace("%%" + b.getParameterId() + "%%", b.getValueAsString());
        }
        return sparql;
    }
}
