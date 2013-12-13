package org.aksw.databugger.sources;

import org.aksw.databugger.Utils.CacheUtils;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.io.TripleDereferenceReader;
import org.aksw.databugger.io.TripleReader;
import org.aksw.databugger.io.TripleReaderFactory;

/**
 * User: Dimitris Kontokostas
 * Source factory
 * Created: 10/3/13 6:45 PM
 */
public class SourceFactory {

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        return createSchemaSourceFromCache(baseFolder, prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceFromCache(String baseFolder, String prefix, String uri, String schema) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.Schema, prefix, uri);
        TripleReader reader = TripleReaderFactory.createFileOrDereferenceTripleReader(cacheFile, schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri) {
        return createSchemaSourceDereference(prefix, uri, uri);
    }

    public static SchemaSource createSchemaSourceDereference(String prefix, String uri, String schema) {
        TripleReader reader = new TripleDereferenceReader(schema);
        return new SchemaSource(prefix, uri, schema, reader);
    }

    public static SchemaSource createEnrichedSchemaSourceFromCache(String baseFolder, String prefix, String uri) {
        String cacheFile = CacheUtils.getSchemaSourceCacheFilename(baseFolder, TestAppliesTo.EnrichedSchema, prefix, uri);
        TripleReader reader = TripleReaderFactory.createFileOrDereferenceTripleReader(cacheFile, uri);
        return new EnrichedSchemaSource(prefix,uri,reader);
    }
}
