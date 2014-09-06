package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;

/**
 * @author Dimitris Kontokostas
 *         Source factory
 * @since 10/3/13 6:45 PM
 */
public final class SourceFactory {

    private SourceFactory() {
    }

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        return createSchemaSourceFromCache(baseFolder, prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri, String schema) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.Schema, prefix, uri);
        RDFReader reader = RDFReaderFactory.createFileOrDereferenceReader(cacheFile, schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri) {
        return createSchemaSourceDereference(prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri, String schema) {
        RDFReader reader = RDFReaderFactory.createDereferenceReader(schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    public static EnrichedSchemaSource createEnrichedSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.EnrichedSchema, prefix, uri);
        RDFReader reader = RDFReaderFactory.createFileOrDereferenceReader(cacheFile, uri);
        return new EnrichedSchemaSource(prefix, uri, reader);
    }

    public static SchemaSource createSchemaSourceFromText(String namespace, String text, String format) {

        String uri = namespace + TestUtils.getMD5FromString(text);
        String prefix = CacheUtils.getAutoPrefixForURI(uri);

        return new SchemaSource(prefix, uri, uri, RDFReaderFactory.createReaderFromText(text, format));
    }
}
