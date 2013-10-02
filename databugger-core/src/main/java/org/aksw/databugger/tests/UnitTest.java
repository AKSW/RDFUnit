package org.aksw.databugger.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.shared.uuid.UUIDFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.DatabuggerUtils;
import org.aksw.databugger.PrefixService;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.enums.TestGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 6:31 AM
 */
public class UnitTest {
    private static Logger log = LoggerFactory.getLogger(UnitTest.class);

    private final String testURI;
    private final String pattern;
    private final TestGeneration generated;
    private final String autoGeneratorURI;
    private final TestAppliesTo appliesTo;
    private final String sourceUri;
    private final TestAnnotation annotation;
    private final String sparql;
    private final String sparqlPrevalence;
    private final List<String> references;

    public UnitTest(String sparql, String sparqlPrevalence, String testURI) {
        this(JenaUUID.generate().asString(), "", TestGeneration.ManuallyGenerated, "", null, "", null, sparql, sparqlPrevalence, new ArrayList<String>());
    }

    public UnitTest(String testURI, String pattern, TestGeneration generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, TestAnnotation annotation, String sparql, String sparqlPrevalence, List<String> references) {
        this.testURI = testURI;
        this.pattern = pattern;
        this.generated = generated;
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = appliesTo;
        this.sourceUri = sourceUri;
        this.annotation = annotation;
        this.sparql = sparql;
        this.sparqlPrevalence = sparqlPrevalence;
        this.references = references;
    }

    public Model getUnitTestModel() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        saveTestToModel(model);
        return model;
    }

    public void saveTestToModel(Model model) {

        Resource resource = model.createResource(testURI)
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "Test"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "basedOnPattern"), model.createResource("tddp:" + getPattern()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "generated"), model.createResource(getGenerated().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "testGenerator"), model.createResource(getAutoGeneratorURI()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "appliesTo"), model.createResource(getAppliesTo().getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "source"), model.createResource(getSourceUri()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "sparql"), getSparql())
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "sparqlPrevalence"), getSparqlPrevalence());

        for (String r : getReferences()) {
            resource.addProperty(model.createProperty(PrefixService.getPrefix("tddo") + "references"), ResourceFactory.createResource(r));
        }

    }

    public UnitTest clone() {
        return new UnitTest(
                testURI,
                pattern,
                generated,
                autoGeneratorURI,
                appliesTo,
                sourceUri,
                annotation,
                sparql,
                sparqlPrevalence,
                references);
    }

    public String getPattern() {
        return pattern;
    }

    public TestGeneration getGenerated() {
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

    public TestAnnotation getAnnotation() {
        return annotation;
    }

    public String getSparql() {
        return sparql;
    }

    public Query getSparqlQuery() {
        return  QueryFactory.create(DatabuggerUtils.getAllPrefixes() + sparql);
    }

    public String getSparqlAsCount() {
        String newSparql = sparql.replaceFirst("SELECT", "SELECT (count( " );
        newSparql = newSparql.replaceFirst("WHERE", ") AS ?total ) WHERE");
        return newSparql;
    }

    public Query getSparqlAsCountQuery() {
        return QueryFactory.create(DatabuggerUtils.getAllPrefixes() + getSparqlAsCount());
    }

    public Query getSparqlAnnotatedQuery() {

        // TODO set construct annotations
        return getSparqlQuery();
    }

    public String getSparqlPrevalence() {
        return sparqlPrevalence;
    }

    public Query getSparqlPrevalenceQuery() {
        return QueryFactory.create(DatabuggerUtils.getAllPrefixes() + sparqlPrevalence);
    }

    public List<String> getReferences() {
        return references;
    }

    public String getTestURI() {
        return testURI;
    }
}
