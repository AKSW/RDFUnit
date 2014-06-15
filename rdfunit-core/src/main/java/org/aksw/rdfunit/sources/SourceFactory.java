package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.DataReader;
import org.aksw.rdfunit.io.DataReaderFactory;
import org.aksw.rdfunit.io.RDFDereferenceReader;

/**
 * User: Dimitris Kontokostas
 * Source factory
 * Created: 10/3/13 6:45 PM
 */
public class SourceFactory {

    private SourceFactory() {}

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        return createSchemaSourceFromCache(baseFolder, prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri, String schema) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.Schema, prefix, uri);
        DataReader reader = DataReaderFactory.createFileOrDereferenceReader(cacheFile, schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri) {
        return createSchemaSourceDereference(prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri, String schema) {
        DataReader reader = new RDFDereferenceReader(schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    public static EnrichedSchemaSource createEnrichedSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.EnrichedSchema, prefix, uri);
        DataReader reader = DataReaderFactory.createFileOrDereferenceReader(cacheFile, uri);
        return new EnrichedSchemaSource(prefix, uri, reader);
    }
}
