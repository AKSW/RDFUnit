package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.utils.StringUtils;
import org.aksw.rdfunit.utils.UriToPathUtils;

/**
 * <p>SourceFactory class.</p>
 *
 * @author Dimitris Kontokostas
 *         Source factory
 * @since 10/3/13 6:45 PM
 * @version $Id: $Id
 */
public final class SchemaSourceFactory {

    private SchemaSourceFactory() {
    }

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        return createSchemaSourceFromCache(baseFolder, prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri, String schema) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.Schema, prefix, uri);
        RdfReader reader = RdfReaderFactory.createFileOrDereferenceReader(cacheFile, schema);
        return createSchemaSourceSimple(prefix, uri, schema, reader);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri) {
        return createSchemaSourceDereference(prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri, String schema) {
        return createSchemaSourceSimple(prefix, uri, schema, RdfReaderFactory.createDereferenceReader(schema));
    }

    public static EnrichedSchemaSource createEnrichedSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.EnrichedSchema, prefix, uri);
        RdfReader reader = RdfReaderFactory.createFileOrDereferenceReader(cacheFile, uri);
        return new EnrichedSchemaSource(new SourceConfig(prefix, uri), reader);
    }

    public static SchemaSource createSchemaSourceFromText(String namespace, String text, String format) {

        String uri = namespace + StringUtils.getHashFromString(text);
        String prefix = UriToPathUtils.getAutoPrefixForURI(uri);

        return createSchemaSourceSimple(prefix, uri, RdfReaderFactory.createReaderFromText(text, format));
    }


    public static SchemaSource createSchemaSourceSimple(String uri) {

        return createSchemaSourceSimple(uri, RdfReaderFactory.createResourceOrFileOrDereferenceReader(uri));
    }


    public static SchemaSource createSchemaSourceSimple(String uri, RdfReader reader) {

        return createSchemaSourceSimple(UriToPathUtils.getAutoPrefixForURI(uri), uri, uri, reader);
    }

    public static SchemaSource createSchemaSourceSimple(String prefix, String uri, RdfReader reader) {

        return createSchemaSourceSimple(prefix, uri, uri, reader);
    }

    public static SchemaSource createSchemaSourceSimple(String prefix, String uri, String schema, RdfReader reader) {

        return new SchemaSource(new SourceConfig(prefix, uri), schema, reader);
    }

    public static SchemaSource copySchemaSource(SchemaSource schemaSource) {

        return new SchemaSource(schemaSource);
    }
}
