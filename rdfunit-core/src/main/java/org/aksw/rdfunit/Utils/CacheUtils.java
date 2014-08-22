package org.aksw.rdfunit.Utils;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.sources.Source;

import java.net.URI;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 9:17 AM
 */
public final class CacheUtils {

    private CacheUtils() {
    }

    public static String getSchemaSourceCacheFilename(String testFolder, TestAppliesTo schemaType, String prefix, String uri) {
        return testFolder + schemaType.name() + "/" + getCacheFolderForURI(uri) + prefix + ".cache." + schemaType.name() + ".ttl";
    }

    public static String getCacheFolderForURI(String uri) {
        String retVal = "";
        try {
            URI tmp = new URI(uri);
            String host = tmp.getHost();
            String path = tmp.getPath();
            retVal = host + path + ( path.endsWith("/") ? "" :  "/");
        } catch (Exception e) {
            // TODO handle exception
        }

        return retVal;
    }

    public static String getAutoPrefixForURI(String uri) {
        return uri.replace("http://", "").replace("https://", "").replaceAll("[?\"'\\/<>*|:#,&]", "_");
    }

    public static String getSourceAutoTestFile(String testFolder, Source source) {
        return getFile(testFolder, source, "tests", source.getSourceType().name());
    }

    public static String getSourceManualTestFile(String testFolder, Source source) {
        return getFile(testFolder, source, "tests", "Manual");
    }

    public static String getCacheFile(String testFolder, Source source) {
        return getFile(testFolder, source, "cache", source.getSourceType().name());
    }

    private static String getFile(String testFolder, Source source, String type, String sourceType) {
        return testFolder + sourceType + "/" + getCacheFolderForURI(source.getUri()) + source.getPrefix() + "." + type + "." + sourceType + ".ttl";
    }

}
