package org.aksw.rdfunit.services;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Collections;
import java.util.Map;

/**
 * A service class that keeps track of the defined prefixes and provides various utils
 *
 * @author Dimitris Kontokostas
  * @since 10/1/13 7:06 PM
 */
public final class PrefixNSService {

    /**
     * Dual map that maps prefixes to namespaces (lazy init)
     */
    private static class MapInstance {
        private static final BidiMap<String, String> prefixNsBidiMap = createPrefixNsBidiMap();
    }

    private static class DeclInstance {
        private static final String sparqlPrefixDecl = createSparqlPrefixes();
    }

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
        return MapInstance.prefixNsBidiMap.get(prefix);
    }

    public static String getPrefixFromNS(final String namespace) {
        return MapInstance.prefixNsBidiMap.getKey(namespace);
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
        return DeclInstance.sparqlPrefixDecl;
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
        return Collections.unmodifiableMap(MapInstance.prefixNsBidiMap);
    }

    protected static BidiMap<String, String> createPrefixNsBidiMap() {

        BidiMap<String, String> dualMap = new DualHashBidiMap<>();
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
            dualMap.put(entry.getKey(), entry.getValue());
        }

        return dualMap;
    }

    /**
     * We use an external prefix map to avoid concurrency issues
     */
    private static String createSparqlPrefixes() {
        StringBuilder sparqlPrefixes = new StringBuilder();
        for (Map.Entry<String, String> entry : MapInstance.prefixNsBidiMap.entrySet()) {
            sparqlPrefixes.append(" PREFIX " + entry.getKey() + ": <" + entry.getValue() + "> \n");
        }
        return sparqlPrefixes.toString();
    }
}
