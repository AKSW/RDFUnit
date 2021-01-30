package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.utils.UriToPathUtils;

/**
 * @author Dimitris Kontokostas
 * @since 11/14/13 9:17 AM
 */
public final class CacheUtils {

  private CacheUtils() {
  }

  public static String getSchemaSourceCacheFilename(String testFolder, TestAppliesTo schemaType,
      String prefix, String uri) {
    return getTestFolder(testFolder) + schemaType.name() + "/" + UriToPathUtils
        .getCacheFolderForURI(uri) + prefix + ".cache." + schemaType.name() + ".ttl";
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

    return getTestFolder(testFolder) + sourceType + "/" + UriToPathUtils
        .getCacheFolderForURI(source.getUri()) +
        source.getPrefix() + "." + type + "." + sourceType + ".ttl";
  }

  private static String getTestFolder(String testFolder) {
    String testFolderWithSlash = testFolder;
    if (!testFolder.endsWith("/")) {
      testFolderWithSlash = testFolderWithSlash + "/";
    }
    return testFolderWithSlash;
  }

}
