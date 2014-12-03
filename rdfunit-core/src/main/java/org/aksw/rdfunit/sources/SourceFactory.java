package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;

/**
 * <p>SourceFactory class.</p>
 *
 * @author Dimitris Kontokostas
 *         Source factory
 * @since 10/3/13 6:45 PM
 * @version $Id: $Id
 */
public final class SourceFactory {

    private SourceFactory() {
    }

    /**
     * <p>createSchemaSourceFromCache.</p>
     *
     * @param baseFolder a {@link java.lang.String} object.
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        return createSchemaSourceFromCache(baseFolder, prefix, uri, uri);
    }

    /**
     * <p>createSchemaSourceFromCache.</p>
     *
     * @param baseFolder a {@link java.lang.String} object.
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param schema a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri, String schema) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.Schema, prefix, uri);
        RDFReader reader = RDFReaderFactory.createFileOrDereferenceReader(cacheFile, schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    /**
     * <p>createSchemaSourceDereference.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource createSchemaSourceDereference(String prefix, String uri) {
        return createSchemaSourceDereference(prefix, uri, uri);
    }

    /**
     * <p>createSchemaSourceDereference.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param schema a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource createSchemaSourceDereference(String prefix, String uri, String schema) {
        RDFReader reader = RDFReaderFactory.createDereferenceReader(schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    /**
     * <p>createEnrichedSchemaSourceFromCache.</p>
     *
     * @param baseFolder a {@link java.lang.String} object.
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.EnrichedSchemaSource} object.
     */
    public static EnrichedSchemaSource createEnrichedSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.EnrichedSchema, prefix, uri);
        RDFReader reader = RDFReaderFactory.createFileOrDereferenceReader(cacheFile, uri);
        return new EnrichedSchemaSource(prefix, uri, reader);
    }

    /**
     * <p>createSchemaSourceFromText.</p>
     *
     * @param namespace a {@link java.lang.String} object.
     * @param text a {@link java.lang.String} object.
     * @param format a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public static SchemaSource createSchemaSourceFromText(String namespace, String text, String format) {

        String uri = namespace + TestUtils.getHashFromString(text);
        String prefix = CacheUtils.getAutoPrefixForURI(uri);

        return new SchemaSource(prefix, uri, uri, RDFReaderFactory.createReaderFromText(text, format));
    }
}
