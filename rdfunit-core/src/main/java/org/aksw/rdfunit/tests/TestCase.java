package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/23/13 6:31 AM
 */
public abstract class TestCase implements Comparable<TestCase> {

    private final String testURI;
    private final TestCaseAnnotation annotation;

    public TestCase(String testURI, TestCaseAnnotation annotation) throws TestCaseInstantiationException {
        this.testURI = testURI;
        this.annotation = annotation;
        // Validate on subclasses
    }

    public Model getUnitTestModel() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        serialize(model);
        return model;
    }

    public abstract String getSparqlWhere();

    public abstract String getSparqlPrevalence();

    public Resource serialize(Model model) {

        Resource resource = model.createResource(testURI);
        annotation.serialize(resource, model);
        return resource;

    }

    public String getResultMessage() {
        return annotation.getDescription();
    }

    public RLOGLevel getLogLevel() {
        return annotation.getTestCaseLogLevel();
    }

    public Collection<ResultAnnotation> getResultAnnotations() {
        return annotation.getResultAnnotations();
    }

    public Collection<ResultAnnotation> getVariableAnnotations() {
        return annotation.getVariableAnnotations();
    }

    public Query getSparqlPrevalenceQuery() {
        if (getSparqlPrevalence().trim().isEmpty())
            return null;
        return QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getSparqlPrevalence());
    }

    public String getTestURI() {
        return testURI;
    }

    public String getAbrTestURI() {
        return testURI.replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:");
    }

    public void validateQueries() throws TestCaseInstantiationException {
        // TODO move this in a separate class

        validateSPARQL(new QueryGenerationSelectFactory().getSparqlQueryAsString(this), "SPARQL");
        validateSPARQL(new QueryGenerationExtendedSelectFactory().getSparqlQueryAsString(this), "SPARQL Extended");
        validateSPARQL(new QueryGenerationCountFactory().getSparqlQueryAsString(this), "SPARQL Count");
        validateSPARQL(new QueryGenerationAskFactory().getSparqlQueryAsString(this), "ASK");
        if (!getSparqlPrevalence().trim().equals("")) { // Prevalence in not always defined
            validateSPARQL(getSparqlPrevalence(), "prevalence");
        }

        Collection<String> vars = new QueryGenerationSelectFactory().getSparqlQuery(this).getResultVars();
        // check for Resource & message
        boolean hasResource = false;
        for (String v : vars) {
            if (v.equals("resource")) {
                hasResource = true;
            }

        }
        if (!hasResource) {
            throw new TestCaseInstantiationException("?resource is not included in SELECT for Test: " + testURI);
        }

        // Message is allowed to exist either in SELECT or as a result annotation
        if (annotation.getDescription().equals("")) {
            throw new TestCaseInstantiationException("No test case dcterms:description message included in TestCase: " + testURI);
        }

        if (getLogLevel() == null) {
            throw new TestCaseInstantiationException("No (or malformed) log level included for Test: " + testURI);
        }
    }

    private void validateSPARQL(String sparql, String type) throws TestCaseInstantiationException {
        try {
            QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + sparql);
        } catch (QueryParseException e) {
            String message = "QueryParseException in " + type + " query (line " + e.getLine() + ", column " + e.getColumn() + " for Test: " + testURI + "\n" + PrefixNSService.getSparqlPrefixDecl() + sparql;
            throw new TestCaseInstantiationException(message, e);
        }
    }

    @Override
    public int compareTo(TestCase o) {
        return this.getTestURI().compareTo(o.getTestURI());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestCase) {
            return this.getTestURI().compareTo(((TestCase) obj).getTestURI()) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return this.getTestURI();
    }

}
