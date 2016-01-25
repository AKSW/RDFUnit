package org.aksw.rdfunit.utils;

import com.hp.hpl.jena.shared.uuid.JenaUUID;

public final class JenaUtils {

    private static final String DEFAULT_PREFIX = "urn:uuid:";

    public static String getUniqueIri(String prefix) {
        return prefix + JenaUUID.generate().asString();
    }

    public static String getUniqueIri() {
        return getUniqueIri(JenaUtils.DEFAULT_PREFIX);
    }

}
