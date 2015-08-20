package org.aksw.rdfunit.sources;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper class to instantiate Sources
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 9:05 PM
 */
class SourceConfig {

    private final String prefix;
    private final String uri;

    SourceConfig(String prefix, String uri) {

        this.prefix = checkNotNull(prefix);
        this.uri = checkNotNull(uri);
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Getter for the field <code>uri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUri() {
        return uri;
    }
}
