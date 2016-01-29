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
class SourceConfig {
    @Getter @NonNull private final String prefix;
    @Getter @NonNull private final String uri;
}
