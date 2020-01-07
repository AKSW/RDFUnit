package org.aksw.rdfunit.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Defines various static text utililities
 *
 * @author Dimitris Kontokostas
 * @since 3/29/15 5:00 PM

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
            byte[] array = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Cannot calculate SHA-256 hash for :" + str, e);
        }
    }

    /**
     * Will find the longest suffix of the first sequence which is a prefix of the second.
     * @param first - first
     * @param second - second
     * @return - the longest overlap
     */
    public static String findLongestOverlap(String first, String second){
        if(org.apache.commons.lang3.StringUtils.isEmpty(first) || org.apache.commons.lang3.StringUtils.isEmpty(second))
            return "";
        int length = Math.min(first.length(), second.length());
        for(int i = 0; i < length; i++){
            String zw = first.substring(first.length() - length + i);
            if(second.startsWith(zw))
                return zw;
        }
        return "";
    }
}
