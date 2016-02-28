package org.aksw.rdfunit.utils;

import java.io.UnsupportedEncodingException;

/**
 * Defines various static text utililities
 *
 * @author Dimitris Kontokostas
 * @since 3/29/15 5:00 PM
 * @version $Id: $Id
 */
public final class StringUtils {
    private StringUtils() {}

    /**
     * Generates a sha-256 hash from a string
     * Taken from http://stackoverflow.com/questions/415953/generate-md5-hash-in-java
     *
     * @param str a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @since 0.7.2
     */
    public static String getHashFromString(String str) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Cannot calculate SHA-256 hash for :" + str, e);
        }
    }
}
