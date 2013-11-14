package org.aksw.databugger.Utils;

import org.aksw.databugger.enums.TestAppliesTo;

import java.net.URI;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 9:17 AM
 */
public class CacheUtils {

    public static String getSchemaSourceCacheFilename(String baseFolder, TestAppliesTo schemaType, String prefix, String uri) {
        return baseFolder + schemaType.name() + "/" + getCacheFolderForURI(uri) + prefix + ".cache." + schemaType.name() + ".ttl";
    }

    private static String getCacheFolderForURI(String uri) {
        String retVal = "";
        try {
            URI tmp = new URI("");
            String host = tmp.getHost();
            String path = tmp.getPath();
            retVal = host + path + "/";
        } catch (Exception e) {
            // TODO handle exception
        }

        return retVal;
    }

}
