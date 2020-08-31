package org.aksw.rdfunit.utils;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/5/15 4:16 AM
 */
public final class UriToPathUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(UriToPathUtils.class);


  private UriToPathUtils() {
  }

  public static String getCacheFolderForURI(String uri) {
    String retVal = "";
    try {
      URI tmp = new URI(uri);
      String host = tmp.getHost();
      String path = tmp.getPath();
      retVal = host + path + (path.endsWith("/") ? "" : "/");
    } catch (URISyntaxException e) {
      LOGGER.debug("Cannot create cache folder for IRI {}", uri, e);
    }

    return retVal;
  }

  public static String getAutoPrefixForURI(String uri) {
    return uri
        .replace("http://", "")
        .replace("https://", "")
        .replaceAll("[?\"'\\/<>*|:#,&]", "_");
  }

}
