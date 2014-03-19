package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.exceptions.TestCaseException;
import org.aksw.rdfunit.tests.results.ResultAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 6:31 AM
 */
public abstract class TestCase implements Comparable<TestCase> {
    protected Logger log = LoggerFactory.getLogger(TestCase.class);

    protected final String testURI;


    protected final TestCaseAnnotation annotation;

    public TestCase(String testURI, TestCaseAnnotation annotation) throws TestCaseException {
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

    public String getSparql() {
        return " SELECT DISTINCT ?resource ?message WHERE " + getSparqlWhere();
    }

    public Query getSparqlQuery() {
        return QueryFactory.create(RDFUnitUtils.getAllPrefixes() + getSparql());
    }

    public String getSparqlAsCount() {
        return " SELECT (count( ?resource ) AS ?total ) WHERE " + getSparqlWhere();
    }

    public Query getSparqlAsCountQuery() {
        return QueryFactory.create(RDFUnitUtils.getAllPrefixes() + getSparqlAsCount());
    }

    public String getSparqlAsAsk() {
        return getSparqlAsAskQuery().toString();
    }

    public Query getSparqlAsAskQuery() {
        Query q = getSparqlQuery();
        q.setQueryAskType();
        return q;
    }

    public Query getSparqlAnnotatedQuery() {

        // TODO set construct annotations
        return getSparqlQuery();
    }

    public String getSparqlAnnotated() {

        // TODO set construct annotations
        return getSparql();
    }

    public String getResultMessage() {
        return annotation.getAnnotationMessage();
    }

    public String getLogLevel() {
        return annotation.getTestCaseLogLevel();
    }

    public List<ResultAnnotation> getResultAnnotations() {
        return annotation.getResultAnnotations();
    }

    public Query getSparqlPrevalenceQuery() {
        return QueryFactory.create(RDFUnitUtils.getAllPrefixes() + getSparqlPrevalence());
    }

    public String getTestURI() {
        return testURI;
    }

    public void validateQueries() throws TestCaseException {
        validateSPARQL(getSparql(), "SPARQL");
        validateSPARQL(getSparqlAsCount(), "SPARQL Count");
        validateSPARQL(getSparqlAsAsk(), "ASK");
        validateSPARQL(getSparqlAnnotated(), "construct");
        if (!getSparqlPrevalence().trim().equals("")) // Prevalence in not always defined
            validateSPARQL(getSparqlPrevalence(), "prevalence");

        List<String> vars = getSparqlQuery().getResultVars();
        // check for Resource & message
        boolean hasResource = false;
        boolean hasMessage = false;
        for (String v : vars) {
            if (v.equals("resource"))
                hasResource = true;
            if (v.equals("message"))
                hasMessage = true;
        }
        if (!hasResource)
            throw new TestCaseException("?resource is not included in SELECT for Test: " + testURI);

        // Message is allowed to exist either in SELECT or as a result annotation
        if (!hasMessage && annotation.getAnnotationMessage().equals(""))
            throw new TestCaseException("No message included in TestCase neither in SELECT nor as ResultAnnotation for Test: " + testURI);

        if (getLogLevel() == null || getLogLevel().equals("")) {
            throw new TestCaseException("No log level included for Test: " + testURI);
        }
    }

    private void validateSPARQL(String sparql, String type) throws TestCaseException {
        try {
            Query q = QueryFactory.create(RDFUnitUtils.getAllPrefixes() + sparql);
        } catch (QueryParseException e) {
            throw new TestCaseException("QueryParseException in " + type + " query (line " + e.getLine() + ", column " + e.getColumn() + " for Test: " + testURI + "\n" + RDFUnitUtils.getAllPrefixes() + sparql);
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
