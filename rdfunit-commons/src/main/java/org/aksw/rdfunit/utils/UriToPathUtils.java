package org.aksw.rdfunit.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/5/15 4:16 AM
 */
public final class UriToPathUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UriToPathUtils.class);


    private UriToPathUtils(){}

    /**
     * <p>getCacheFolderForURI.</p>
     *
     * @param uri a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
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

    /**
     * <p>getAutoPrefixForURI.</p>
     *
     * @param uri a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getAutoPrefixForURI(String uri) {
        return uri
                .replace("http://", "")
                .replace("https://", "")
                .replaceAll("[?\"'\\/<>*|:#,&]", "_");
    }

}
