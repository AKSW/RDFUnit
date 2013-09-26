package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.enums.TestGeneration;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/23/13 6:31 AM
 */
public class UnitTest {
    public final String pattern;
    public final TestGeneration generated;
    public final String autoGeneratorURI;
    public final TestAppliesTo appliesTo;
    public final String sourceUri;
    public final TestAnnotation annotation;
    public final String sparql;
    public final String sparqlPrevalence;

    public UnitTest(String sparql, String sparqlPrevalence) {
        this("", TestGeneration.ManuallyGenerated, "", null, "", null, sparql, sparqlPrevalence);
    }

    public UnitTest(String pattern, TestGeneration generated, String autoGeneratorURI, TestAppliesTo appliesTo, String sourceUri, TestAnnotation annotation, String sparql, String sparqlPrevalence) {
        this.pattern = pattern;
        this.generated = generated;
        this.autoGeneratorURI = autoGeneratorURI;
        this.appliesTo = appliesTo;
        this.sourceUri = sourceUri;
        this.annotation = annotation;
        this.sparql = sparql;
        this.sparqlPrevalence = sparqlPrevalence;
    }

    public Model getUnitTestModel(){
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        saveTestToModel(model);
        return model;
    }

    public void saveTestToModel(Model model) {

        model.createResource()
                .addProperty(RDF.type, model.createResource("tddo:Test"))
                .addProperty(model.createProperty("tddo:pattern"), model.createResource("tddp:" + pattern))
                .addProperty(model.createProperty("tddo:generated"),model.createResource(generated.getUri()))
                .addProperty(model.createProperty("tddo:testGenerator"),model.createResource(autoGeneratorURI))
                .addProperty(model.createProperty("tddo:appliesTo"),model.createResource(appliesTo.getUri()))
                .addProperty(model.createProperty("tddo:source"),model.createResource(sourceUri))
                .addProperty(model.createProperty("tddo:sparql"), sparql)
                .addProperty(model.createProperty("tddo:sparqlPrevalence"),sparqlPrevalence)
                ;

    }
}
