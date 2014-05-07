package org.aksw.rdfunit.services;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.cache.staging.CacheBackendDao;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Dimitris Kontokostas
 * Keeps a list of all prefixes used in the project
 * Created: 10/1/13 7:06 PM
 */
public class PrefixService {
    final private static HashMap<String, String> prefixes = new HashMap<String, String>();


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

    public static HashMap<String, String> getPrefixes() {
        // initialize prefixes on first run
        if (prefixes.isEmpty())
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
                    for (String id : prf.keySet()) {
                        prefixes.put(id, prf.get(id));
                    }
                }
            }
        return prefixes;
    }
}
