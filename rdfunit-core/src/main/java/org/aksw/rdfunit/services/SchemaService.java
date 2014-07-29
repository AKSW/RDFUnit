package org.aksw.rdfunit.services;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SourceFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 10/2/13 12:24 PM
 */
public final class SchemaService {
    final private static Map<String, String> schemata = new ConcurrentHashMap<>();

    private SchemaService() {
    }

    public static void addSchemaDecl(String id, String uri, String url) {
        schemata.put(id, uri + "\t" + url);
    }

    public static void addSchemaDecl(String id, String uri) {
        schemata.put(id, uri);
    }

    public static SchemaSource getSource(String id) {
        return getSource(null, id);
    }

    public static SchemaSource getSource(String baseFolder, String id) {
        String sourceUriURL = schemata.get(id);
        if (sourceUriURL == null) {
            // If not a prefix try to dereference it
            if (id.contains("/") || id.contains("\\")) {
                return SourceFactory.createSchemaSourceDereference(CacheUtils.getAutoPrefixForURI(id), id);
            } else {
                return null;
            }
        }

        String[] split = sourceUriURL.split("\t");
        if (split.length == 2) {
            if (baseFolder != null) {
                return SourceFactory.createSchemaSourceFromCache(baseFolder, id, split[0], split[1]);
            } else {
                return SourceFactory.createSchemaSourceDereference(id, split[0], split[1]);
            }
        } else {
            if (baseFolder != null) {
                return SourceFactory.createSchemaSourceFromCache(baseFolder, id, split[0]);
            } else {
                return SourceFactory.createSchemaSourceDereference(id, split[0]);
            }
        }
    }

    public static Collection<SchemaSource> getSourceList(String baseFolder, Collection<String> ids) throws UndefinedSchemaException {
        Collection<SchemaSource> sources = new ArrayList<>();
        for (String id : ids) {
            SchemaSource src = getSource(baseFolder, id.trim());
            if (src != null) {
                sources.add(src);
            } else {
                throw new UndefinedSchemaException(id);
            }
        }
        return sources;
    }

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

