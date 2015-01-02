package org.aksw.rdfunit.sources;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 1/2/15 7:57 PM
 */
public class TestSourceFactory {
    public static TestSource createTestSource(TestSource source, Collection<SchemaSource> schemata) {
        if (source instanceof DumpTestSource) {
            return new DumpTestSource((DumpTestSource) source, schemata);
        }
        if (source instanceof EndpointTestSource) {
            return new EndpointTestSource((EndpointTestSource) source, schemata);
        }

        throw new IllegalArgumentException("Cannot initialize TestSource");
    }
}
