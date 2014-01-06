package org.aksw.databugger.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 6:31 AM
 */
public abstract class TestCase implements Comparable<TestCase> {
    private static Logger log = LoggerFactory.getLogger(TestCase.class);

    protected final String testURI;


    protected final TestCaseAnnotation annotation;

    public TestCase(String testURI, TestCaseAnnotation annotation) {
        this.testURI = testURI;
        this.annotation = annotation;
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
        return " SELECT DISTINCT ?resource ?message ?logLevel  WHERE " + getSparqlWhere();
    }

    public Query getSparqlQuery() {
        return QueryFactory.create(DatabuggerUtils.getAllPrefixes() + getSparql());
    }

    public String getSparqlAsCount() {
        return " SELECT (count( ?resource ) AS ?total ) WHERE " + getSparqlWhere();
    }

    public Query getSparqlAsCountQuery() {
        return QueryFactory.create(DatabuggerUtils.getAllPrefixes() + getSparqlAsCount());
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

    public Query getSparqlPrevalenceQuery() {
        return QueryFactory.create(DatabuggerUtils.getAllPrefixes() + getSparqlPrevalence());
    }

    public String getTestURI() {
        return testURI;
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
