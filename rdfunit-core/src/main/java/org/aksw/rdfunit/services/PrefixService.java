package org.aksw.rdfunit.services;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Dimitris Kontokostas
 * Keeps a list of all prefixes used in the project.
 * In addition it is used to generate the SPARQL prefixes for all the queries and set the NS Prefix Map is a Model
 * Created: 10/1/13 7:06 PM
 */
public final class PrefixService {
    /**
     * Bidirectional Map to get both values and keys
     */
    final private static BidiMap<String, String> prefixes = new DualHashBidiMap<>();

    private static String sparqlPrefixDecl = null;

    private PrefixService() {
    }

    /* We don't need this atm but if we add it need to deal with concurrency
    public static void addPrefix(String prefix, String uri) {
        getPrefixes().put(prefix, uri);
    } */

    public static String getNSFromPrefix(String id) {
        return getPrefixes().get(id);
    }

    public static String getPrefixFromNS(String namespace) {
        return getPrefixes().getKey(namespace);
    }

    public static Set<String> getPrefixList() {
        return getPrefixes().keySet();
    }

    public static void setNSPrefixesInModel(Model model) {
        model.setNsPrefixes(getPrefixMap());
    }

    public static String getSparqlPrefixDecl() {
        // Works only when we init prefixes on startup only
        // If another prefix is added after first generation it will be invalid
        if (sparqlPrefixDecl == null) {
            // If no prefixes exist return ""
            Map<String, String> prefixCopy = getPrefixes();
            if (prefixCopy.isEmpty()) {
                return "";
            } else {
                synchronized (PrefixService.class) {
                    if (sparqlPrefixDecl == null) {
                        sparqlPrefixDecl = generateSparqlPrefixes(prefixCopy);
                    }
                }
            }
        }
        return sparqlPrefixDecl;

    }

    private static Map<String, String> getPrefixMap() {
        return getPrefixes();
    }

    private static BidiMap<String, String> getPrefixes() {
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
                        // Use local filed and NOT accessor method (synchronized)
                        prefixes.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return prefixes;
    }

    /**
     * We use an external prefix map to avoid concurrency issues
     */
    private static String generateSparqlPrefixes(Map<String,String> prefixMap) {
        StringBuilder sparqlPrefixes = new StringBuilder();
        for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
            sparqlPrefixes.append(" PREFIX " + entry.getKey() + ": <" + entry.getValue() + "> \n");
        }
        return sparqlPrefixes.toString();
    }
}
