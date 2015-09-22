package org.aksw.rdfunit.junit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.runner.RunWith;

@RunWith(RdfUnitJunitRunner.class)
@Ontology(uri=Constants.FOAF_ONTOLOGY_URI, format="rdf")
public class RunnerTest {

    @InputModel
    public Model getInputData() {
        return ModelFactory
                .createDefaultModel()
                .read("https://raw.githubusercontent.com/RDFLib/rdflib/master/examples/foaf.rdf");
    }

}
