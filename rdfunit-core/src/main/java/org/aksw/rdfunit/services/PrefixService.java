package org.aksw.rdfunit.services;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Dimitris Kontokostas
 * Keeps a list of all prefixes used in the project
 * Created: 10/1/13 7:06 PM
 */
public final class PrefixService {
    final private static Map<String, String> prefixes = new HashMap<String, String>();


    private PrefixService() {
    }

    public static void addPrefix(String prefix, String uri) {
        getPrefixes().put(prefix, uri);
    }

    public static String getPrefix(String id) {
        return getPrefixes().get(id);
    }

    public static Set<String> getPrefixList() {
        return getPrefixes().keySet();
    }

    public static Map<String, String> getPrefixMap() {
        return getPrefixes();
    }

    public static Map<String, String> getPrefixes() {
        // initialize prefixes on first run
        if (prefixes.isEmpty()) {
            synchronized (PrefixService.class) {
                if (prefixes.isEmpty()) {
                    Model prefixModel = ModelFactory.createDefaultModel();
                    try {
                        prefixModel.read(PrefixService.class.getResourceAsStream("/org/aksw/rdfunit/prefixes.ttl"), null, "TURTLE");
                    } catch (Exception e) {
                        // TODO handle exception
                    }

                    // Update Prefix Service
                    Map<String, String> prf = prefixModel.getNsPrefixMap();
                    for (Map.Entry<String, String> entry : prf.entrySet()) {
                        prefixes.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return prefixes;
    }
}
