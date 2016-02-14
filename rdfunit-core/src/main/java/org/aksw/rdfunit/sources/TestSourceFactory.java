package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.io.reader.RdfReader;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 1/2/15 7:57 PM
 * @version $Id: $Id
 */
public final class TestSourceFactory {
    private TestSourceFactory() {
    }

    /**
     * <p>createTestSource.</p>
     *
     * @param source a {@link org.aksw.rdfunit.sources.TestSource} object.
     * @param schemata a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     */
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

    /**
     * <p>createDumpTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param dumpReader a {@link RdfReader} object.
     * @param referenceSchemata a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     * @since 0.7.6
     */
    public static TestSource createDumpTestSource(String prefix, String uri, RdfReader dumpReader, Collection<SchemaSource> referenceSchemata) {
        return new TestSourceBuilder()
                .setImMemSingle()
                .setPrefixUri(prefix, uri)
                .setInMemReader(dumpReader)
                .setReferenceSchemata(referenceSchemata)
                .build();
    }

    /**
     * <p>createDatasetTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param dumpReader a {@link RdfReader} object.
     * @param referenceSchemata a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.sources.TestSource} object.
     * @since 0.7.6
     */
    public static TestSource createDatasetTestSource(String prefix, String uri, RdfReader dumpReader, Collection<SchemaSource> referenceSchemata) {
        return new TestSourceBuilder()
                .setImMemDataset()
                .setPrefixUri(prefix, uri)
                .setInMemReader(dumpReader)
                .setReferenceSchemata(referenceSchemata)
                .build();
    }
}
