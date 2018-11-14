package org.aksw.rdfunit.sources;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * Helper class to instantiate Sources
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 9:05 PM
 */
@Value
public class SourceConfig {
    @Getter @NonNull private final String prefix;
    @Getter @NonNull private final String uri;

    @Override
    public int hashCode() {
        return (prefix + uri).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || ! this.getClass().isInstance(obj)){
            return false;
        }
        else{
            SourceConfig zw = (SourceConfig)obj;
            return this.prefix.equals(zw.prefix) && this.uri.equals(zw.uri);
        }
    }
}
