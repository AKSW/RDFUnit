package org.aksw.databugger.sources;

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
        String sourceUriURL = schemata.get(id);
        if (sourceUriURL == null)
            return null;
        String[] split = sourceUriURL.split("\t");
        if (split.length == 2)
            return new SchemaSource(id, split[0], split[1]);
        else
            return new SchemaSource(id, split[0]);
    }

    public  static List<SchemaSource> getSourceList(List<String> ids) {
        List<SchemaSource> sources = new ArrayList<SchemaSource>();
        for (String id: ids) {
            SchemaSource src = getSource(id);
            if (src != null)
                sources.add(src);
        }
        return sources;
    }
}

