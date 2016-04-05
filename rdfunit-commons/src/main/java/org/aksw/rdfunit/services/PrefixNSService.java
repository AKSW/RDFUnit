package org.aksw.rdfunit.services;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.aksw.rdfunit.Resources;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * A service class that keeps track of the defined prefixes and provides various utils
 *
 * @author Dimitris Kontokostas
 * @since 10/1/13 7:06 PM
 * @version $Id: $Id
 */
public final class PrefixNSService {

    /**
     * Dual map that maps prefixes to namespaces (lazy init)
     */
    private static final class MapInstance {
        private static final BiMap<String, String> prefixNsBidiMap = createPrefixNsBidiMap();

        private MapInstance(){}

        private static BiMap<String, String> createPrefixNsBidiMap() {

            BiMap<String, String> dualMap = HashBiMap.create();
            Model prefixModel = ModelFactory.createDefaultModel();

            try (InputStream is = PrefixNSService.class.getResourceAsStream(Resources.PREFIXES)) {
                prefixModel.read(is, null, "TURTLE");
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot read prefixes.ttl from resources", e);
            }

            // Update Prefix Service
            Map<String, String> prf = prefixModel.getNsPrefixMap();
            for (Map.Entry<String, String> entry : prf.entrySet()) {
                // Use local filed and NOT accessor method (synchronized)
                dualMap.put(entry.getKey(), entry.getValue());
            }

            return dualMap;
        }
    }

    private static final class DeclInstance {
        private static final String SPARQL_PREFIX_DECL = createSparqlPrefixes();

        private DeclInstance(){}
        /**
         * We use an external prefix map to avoid concurrency issues
         */
        private static String createSparqlPrefixes() {
            StringBuilder sparqlPrefixes = new StringBuilder();
            for (Map.Entry<String, String> entry : MapInstance.prefixNsBidiMap.entrySet()) {
                sparqlPrefixes.append(" PREFIX ").append(entry.getKey()).append(": <").append(entry.getValue()).append("> \n");
            }
            return sparqlPrefixes.toString();
        }
    }

    private PrefixNSService() {}

    /**
     * Given a prefix it returns the prefix namespace.
     *
     * @param prefix the prefix we want to get
     * @return the namespace or null if it does not exists
     */
    public static String getNSFromPrefix(final String prefix) {
        return MapInstance.prefixNsBidiMap.get(prefix);
    }

    /**
     * <p>getPrefixFromNS.</p>
     *
     * @param namespace a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getPrefixFromNS(final String namespace) {
        return MapInstance.prefixNsBidiMap.inverse().get(namespace);
    }

    /**
     * Adds the defined prefixes in a give model
     *
     * @param model the model we want to initialize
     */
    public static void setNSPrefixesInModel(Model model) {
        model.setNsPrefixes(getPrefixMap());
    }

    /**
     * <p>getSparqlPrefixDecl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getSparqlPrefixDecl() {
        return DeclInstance.SPARQL_PREFIX_DECL;
    }

    /**
     * Given an abbreviated URI, it returns a full URI
     *
     * @param abbreviation the abbreviation
     * @return the URI
     */
    public static String getURIFromAbbrev(final String abbreviation) {
        String[] parts = abbreviation.split(":");
        if (parts.length == 2) {
            return getNSFromPrefix(parts[0]) + parts[1];
        }
        throw new IllegalArgumentException("Undefined prefix in " + abbreviation);
    }


    /**
     * Returns the local name of a URI by removing the prefix namespace
     *
     * @param uri    the uri
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


}
