package org.aksw.rdfunit.sources;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.utils.UriToPathUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the schema definitions
 * It is usually instantiated from LOV on app startup
 *
 * @author Dimitris Kontokostas
 * @since 10/2/13 12:24 PM
 * @version $Id: $Id
 */
public final class SchemaService {
    /**
     * Creates a Bi-Directional map between prefix & namespace
     */
    final private static BiMap<String, String> schemata = HashBiMap.create();

    /**
     * if namespace is different from the ontology uri, we keep it in this map
     */
    final private static Map<String,String> definedBy = new HashMap<>();

    private SchemaService() {
    }

    /**
     * <p>getSize.</p>
     *
     * @return a int.
     */
    public static int getSize() {return schemata.size();}

    /**
     * <p>addSchemaDecl.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param url a {@link java.lang.String} object.
     */
    public static void addSchemaDecl(String prefix, String uri, String url) {
        schemata.forcePut(prefix, uri);
        definedBy.put(uri, url);
    }

    /**
     * <p>addSchemaDecl.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public static void addSchemaDecl(String prefix, String uri) {
        schemata.put(prefix, uri);
    }

    /**
     * <p>getSourceFromUri.</p>
     *
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource getSourceFromUri(String uri) {
        return getSourceFromUri(null, uri);
    }

    /**
     * <p>getSourceFromUri.</p>
     *
     * @param baseFolder a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource getSourceFromUri(String baseFolder, String uri) {
        String prefix = schemata.inverse().get(uri);
        if (prefix == null) {
            return null;
        }

        return getSourceFromPrefix(baseFolder, prefix);
    }

    /**
     * <p>getSourceFromPrefix.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource getSourceFromPrefix(String prefix) {
        return getSourceFromPrefix(null, prefix);
    }

    /**
     * <p>getSourceFromPrefix.</p>
     *
     * @param baseFolder a {@link java.lang.String} object.
     * @param prefix a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource getSourceFromPrefix(String baseFolder, String prefix) {
        String namespace = schemata.get(prefix);
        if (namespace == null) {
            // If not a prefix try to dereference it
            if (prefix.contains("/") || prefix.contains("\\")) {
                return SchemaSourceFactory.createSchemaSourceDereference(UriToPathUtils.getAutoPrefixForURI(prefix), prefix);
            } else {
                return null;
            }
        }

        String isDefinedBy = definedBy.get(namespace);


        if (isDefinedBy != null && !isDefinedBy.isEmpty()) {
            if (baseFolder != null) {
                return SchemaSourceFactory.createSchemaSourceFromCache(baseFolder, prefix, namespace, isDefinedBy);
            } else {
                return SchemaSourceFactory.createSchemaSourceDereference(prefix, namespace, isDefinedBy);
            }
        } else {
            if (baseFolder != null) {
                return SchemaSourceFactory.createSchemaSourceFromCache(baseFolder, prefix, namespace);
            } else {
                return SchemaSourceFactory.createSchemaSourceDereference(prefix, namespace);
            }
        }
    }

    /**
     * <p>getSourceList.</p>
     *
     * @param baseFolder a {@link java.lang.String} object.
     * @param prefixes a {@link java.util.Collection} object.
     * @return a {@link java.util.Collection} object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSchemaException if any.
     */
    public static Collection<SchemaSource> getSourceList(String baseFolder, Collection<String> prefixes) throws UndefinedSchemaException {
        Collection<SchemaSource> sources = new ArrayList<>();
        for (String id : prefixes) {
            SchemaSource src = getSourceFromPrefix(baseFolder, id.trim());
            if (src != null) {
                sources.add(src);
            } else {
                throw new UndefinedSchemaException(id);
            }
        }
        return sources;
    }

    /**
     * <p>getSourceListAll.</p>
     *
     * @param fileCache a boolean.
     * @param baseFolder a {@link java.lang.String} object.
     * @return a {@link java.util.Collection} object.
     * @throws org.aksw.rdfunit.exceptions.UndefinedSchemaException if any.
     */
    public static Collection<SchemaSource> getSourceListAll(boolean fileCache, String baseFolder) throws UndefinedSchemaException {
        Collection<String> prefixes = new ArrayList<>();
        prefixes.addAll(schemata.keySet());

        if (fileCache) {
            return getSourceList(baseFolder, prefixes);
        } else {
            return getSourceList(null, prefixes);
        }
    }
}

