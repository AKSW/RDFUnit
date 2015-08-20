package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.io.reader.RDFReader;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 1/2/15 7:57 PM
 */
public final class TestSourceFactory {
    private TestSourceFactory() {
    }

    public static TestSource createTestSource(TestSource source, Collection<SchemaSource> schemata) {
        if (source instanceof DumpTestSource) {
            return new DumpTestSource((DumpTestSource) source, schemata);
        }
        if (source instanceof DatasetTestSource) {
            return new DatasetTestSource((DatasetTestSource) source, schemata);
        }
        if (source instanceof EndpointTestSource) {
            return new EndpointTestSource((EndpointTestSource) source, schemata);
        }

        throw new IllegalArgumentException("Cannot initialize TestSource");
    }

    public static TestSource createDumpTestSource(String prefix, String uri, RDFReader dumpReader, Collection<SchemaSource> referenceSchemata) {
        return new TestSourceBuilder()
                .setImMemSingle()
                .setPrefixUri(prefix, uri)
                .setInMemReader(dumpReader)
                .setReferenceSchemata(referenceSchemata)
                .build();
    }

    public static TestSource createDatasetTestSource(String prefix, String uri, RDFReader dumpReader, Collection<SchemaSource> referenceSchemata) {
        return new TestSourceBuilder()
                .setImMemDataset()
                .setPrefixUri(prefix, uri)
                .setInMemReader(dumpReader)
                .setReferenceSchemata(referenceSchemata)
                .build();
    }
}
