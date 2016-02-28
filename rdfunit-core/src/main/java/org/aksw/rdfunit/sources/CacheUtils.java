package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.utils.UriToPathUtils;

/**
 * <p>CacheUtils class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 9:17 AM
 * @version $Id: $Id
 */
public final class CacheUtils {

    private CacheUtils() {
    }

    /**
     * <p>getSchemaSourceCacheFilename.</p>
     *
     * @param testFolder a {@link java.lang.String} object.
     * @param schemaType a {@link org.aksw.rdfunit.enums.TestAppliesTo} object.
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getSchemaSourceCacheFilename(String testFolder, TestAppliesTo schemaType, String prefix, String uri) {
        return testFolder + schemaType.name() + "/" + UriToPathUtils.getCacheFolderForURI(uri) + prefix + ".cache." + schemaType.name() + ".ttl";
    }

    /**
     * <p>getSourceAutoTestFile.</p>
     *
     * @param testFolder a {@link java.lang.String} object.
     * @param source a {@link org.aksw.rdfunit.sources.Source} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getSourceAutoTestFile(String testFolder, Source source) {
        return getFile(testFolder, source, "tests", source.getSourceType().name());
    }

    /**
     * <p>getSourceManualTestFile.</p>
     *
     * @param testFolder a {@link java.lang.String} object.
     * @param source a {@link org.aksw.rdfunit.sources.Source} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getSourceManualTestFile(String testFolder, Source source) {
        return getFile(testFolder, source, "tests", "Manual");
    }

    /**
     * <p>getCacheFile.</p>
     *
     * @param testFolder a {@link java.lang.String} object.
     * @param source a {@link org.aksw.rdfunit.sources.Source} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getCacheFile(String testFolder, Source source) {
        return getFile(testFolder, source, "cache", source.getSourceType().name());
    }

    private static String getFile(String testFolder, Source source, String type, String sourceType) {
        return testFolder + sourceType + "/" + UriToPathUtils.getCacheFolderForURI(source.getUri()) + source.getPrefix() + "." + type + "." + sourceType + ".ttl";
    }

}
