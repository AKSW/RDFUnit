package org.aksw.databugger.services;

import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.SourceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        return getSource(null,id);
    }

    public static SchemaSource getSource(String baseFolder, String id) {
        String sourceUriURL = schemata.get(id);
        if (sourceUriURL == null)
            return null;
        String[] split = sourceUriURL.split("\t");
        if (split.length == 2) {
            if (baseFolder != null)
                return SourceFactory.createSchemaSourceFromCache(baseFolder, id, split[0], split[1]);
            else
                return SourceFactory.createSchemaSourceDereference(id, split[0], split[1]);
        }
        else {
            if (baseFolder != null)
                return SourceFactory.createSchemaSourceFromCache(baseFolder, id, split[0]);
            else
                return SourceFactory.createSchemaSourceDereference(id, split[0]);
        }
    }

    public static List<SchemaSource> getSourceList(String baseFolder, List<String> ids) {
        List<SchemaSource> sources = new ArrayList<SchemaSource>();
        for (String id : ids) {
            SchemaSource src = getSource(baseFolder, id);
            if (src != null)
                sources.add(src);
        }
        return sources;
    }

    public static List<SchemaSource> getSourceListAll(boolean fileCache, String baseFolder) {
        List<String> prefixes = new ArrayList<String>();
        prefixes.addAll(schemata.keySet());

        if (fileCache)
            return getSourceList(baseFolder, prefixes);
        else
            return getSourceList(null, prefixes);
    }
}

