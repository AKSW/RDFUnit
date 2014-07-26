package org.aksw.rdfunit.services;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Map;

/**
 * A service class that keeps track of the defined prefixes and provides various utils
 *
 * @author Dimitris Kontokostas
 *         Keeps a list of all prefixNsBidiMap used in the project.
 *         In addition it is used to generate the SPARQL prefixNsBidiMap for all the queries and set the NS Prefix Map is a Model
 * @since 10/1/13 7:06 PM
 */
public final class PrefixNSService {
    /**
     * Bidirectional Map<Prefix, Namespace>
     */
    final private static BidiMap<String, String> prefixNsBidiMap = new DualHashBidiMap<>();

    private static String sparqlPrefixDecl = null;

    private PrefixNSService() {
    }

    /* We don't need this atm but if we add it need to deal with concurrency
    public static void addPrefix(String prefix, String uri) {
        getPrefixNsBidiMap().put(prefix, uri);
    } */

    /**
     * Given a prefix it returns the prefix namespace.
     *
     * @param prefix the prefix we want to get
     * @return the namespace or null if it does not exists
     */
    public static String getNSFromPrefix(final String prefix) {
        return getPrefixNsBidiMap().get(prefix);
    }

    public static String getPrefixFromNS(final String namespace) {
        return getPrefixNsBidiMap().getKey(namespace);
    }

    /**
     * Adds the defined prefixes in a give model
     *
     * @param model the model we want to initialize
     */
    public static void setNSPrefixesInModel(Model model) {
        model.setNsPrefixes(getPrefixMap());
    }

    public static String getSparqlPrefixDecl() {
        // Works only when we init prefixNsBidiMap on startup only
        // If another prefix is added after first generation it will be invalid
        if (sparqlPrefixDecl == null) {
            // If no prefixNsBidiMap exist return ""
            Map<String, String> prefixCopy = getPrefixNsBidiMap();
            if (prefixCopy.isEmpty()) {
                return "";
            } else {
                synchronized (PrefixNSService.class) {
                    if (sparqlPrefixDecl == null) {
                        sparqlPrefixDecl = generateSparqlPrefixes(prefixCopy);
                    }
                }
            }
        }
        return sparqlPrefixDecl;

    }

    /**
     * Given an abbreviated URI, it returns a full URI
     *
     * @param abbreviation the abbreviation
     * @return the URI
     */
    public static String getURIFromAbbrev(final String abbreviation) {
        String [] parts = abbreviation.split(":");
        if (parts.length == 2) {
            return getNSFromPrefix(parts[0]) + parts[1];
        }
        throw new IllegalArgumentException("Undefined prefix in " + abbreviation);
    }

    /*
    public static String getLocalName(String uri) {

    } */


    /**
     * Returns the local name of a URI by removing the prefix namespace
     *
     * @param uri the uri
     * @param prefix the prefix we want to remove
     * @return the local name (uri without prefix namespace)
     */
    public static String getLocalName(final String uri, final String prefix) {
        String ns = getNSFromPrefix(prefix);
        if (ns != null) {
            return uri.replace(ns, "");
        }
        throw new IllegalArgumentException("Undefined prefix (" + prefix + ") in URI: " + uri);
    }

    private static Map<String, String> getPrefixMap() {
        return getPrefixNsBidiMap();
    }

    protected static BidiMap<String, String> getPrefixNsBidiMap() {
        // initialize prefixNsBidiMap on first run
        if (prefixNsBidiMap.isEmpty()) {
            synchronized (PrefixNSService.class) {
                if (prefixNsBidiMap.isEmpty()) {
                    Model prefixModel = ModelFactory.createDefaultModel();
                    try {
                        prefixModel.read(PrefixNSService.class.getResourceAsStream("/org/aksw/rdfunit/prefixes.ttl"), null, "TURTLE");
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot read prefixes.ttl from resources", e);
                    }

                    // Update Prefix Service
                    Map<String, String> prf = prefixModel.getNsPrefixMap();
                    for (Map.Entry<String, String> entry : prf.entrySet()) {
                        // Use local filed and NOT accessor method (synchronized)
                        prefixNsBidiMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return prefixNsBidiMap;
    }

    /**
     * We use an external prefix map to avoid concurrency issues
     */
    private static String generateSparqlPrefixes(Map<String, String> prefixMap) {
        StringBuilder sparqlPrefixes = new StringBuilder();
        for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
            sparqlPrefixes.append(" PREFIX " + entry.getKey() + ": <" + entry.getValue() + "> \n");
        }
        return sparqlPrefixes.toString();
    }
}
