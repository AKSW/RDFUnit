package org.aksw.rdfunit.commons;

import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Wrapper Model factory methods
 *
 * @author Dimitris Kontokostas
 * @since 13/2/2016 12:30 μμ
 */
public final class RdfUnitModelFactory {

    private RdfUnitModelFactory() {}

    public static Model createDefaultModel() {
        Model model = ModelFactory.createDefaultModel();
        PrefixNSService.setNSPrefixesInModel(model);

        return model;
    }
}
