package org.aksw.rdfunit.utils;

import org.aksw.rdfunit.commons.RdfUnitModelFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.uuid.JenaUUID;

import java.net.URI;

public final class JenaUtils {

    private static final String DEFAULT_PREFIX = "urn:uuid:";

    private JenaUtils(){}

    public static String getUniqueIri(String prefix) {
        return prefix + JenaUUID.generate().asString();
    }

    public static String getUniqueIri() {
        return getUniqueIri(JenaUtils.DEFAULT_PREFIX);
    }

    public static Model readModel(URI uri) {

        Model model = RdfUnitModelFactory.createDefaultModel();
        RDFDataMgr.read(model, uri.toString());
        return model;
    }
}
