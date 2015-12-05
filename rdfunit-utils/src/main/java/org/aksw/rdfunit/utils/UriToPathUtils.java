package org.aksw.rdfunit.utils;

import java.net.URI;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 12/5/15 4:16 AM
 */
public final class UriToPathUtils {

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
        } catch (Exception e) {
            // TODO handle exception
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
