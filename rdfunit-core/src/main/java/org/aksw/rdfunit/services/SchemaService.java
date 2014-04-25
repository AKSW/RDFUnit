package org.aksw.rdfunit.services;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SourceFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 10/2/13 12:24 PM
 */
public class SchemaService {
    final private static HashMap<String, String> schemata = new HashMap<String, String>();

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
            return SourceFactory.createSchemaSourceDereference(CacheUtils.getAutoPrefixForURI(id), id);
        }

        String[] split = sourceUriURL.split("\t");
        if (split.length == 2) {
            if (baseFolder != null)
                return SourceFactory.createSchemaSourceFromCache(baseFolder, id, split[0], split[1]);
            else
                return SourceFactory.createSchemaSourceDereference(id, split[0], split[1]);
        } else {
            if (baseFolder != null)
                return SourceFactory.createSchemaSourceFromCache(baseFolder, id, split[0]);
            else
                return SourceFactory.createSchemaSourceDereference(id, split[0]);
        }
    }

    public static java.util.Collection<SchemaSource> getSourceList(String baseFolder, java.util.Collection<String> ids) {
        java.util.Collection<SchemaSource> sources = new ArrayList<SchemaSource>();
        for (String id : ids) {
            SchemaSource src = getSource(baseFolder, id.trim());
            if (src != null)
                sources.add(src);
        }
        return sources;
    }

    public static java.util.Collection<SchemaSource> getSourceListAll(boolean fileCache, String baseFolder) {
        java.util.Collection<String> prefixes = new ArrayList<String>();
        prefixes.addAll(schemata.keySet());

        if (fileCache)
            return getSourceList(baseFolder, prefixes);
        else
            return getSourceList(null, prefixes);
    }
}

