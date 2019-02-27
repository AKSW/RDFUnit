package org.aksw.rdfunit.sources;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper class to instantiate Sources
 *
 * @author Dimitris Kontokostas
 * @since 8/19/15 9:05 PM
 */
public class SourceConfig {
    @Getter @NonNull private final String prefix;
    @Getter @NonNull private final String uri;
    @Getter private final ArrayList<SourceConfig> importedSchemata = Lists.newArrayList();

    public SourceConfig(String prefix, String uri){
        this.uri = uri;
        this.prefix = prefix;
    }

    public SourceConfig(String prefix, String uri, Collection<SourceConfig> imports){
        this(prefix, uri);
        this.importedSchemata.addAll(imports);
    }

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
