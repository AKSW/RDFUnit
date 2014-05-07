package org.aksw.rdfunit;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.io.DataReaderFactory;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.DatasetSource;
import org.aksw.rdfunit.sources.DumpSource;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * Holds a configuration for a complete test
 * Created: 11/15/13 11:50 AM
 */
public class RDFUnitConfiguration {

    private final String prefix;
    private final String uri;
    private final String endpoint;

    // multiple graphs separated with '|'
    private final java.util.Collection<String> graphs;

    private final String dereferenceUri;

    private final java.util.Collection<SchemaSource> sources;

    /* For Endpoint source */
    public RDFUnitConfiguration(String prefix, String uri, String endpoint, java.util.Collection<String> graphs, java.util.Collection<SchemaSource> sources) {
        this.prefix = prefix;
        this.uri = uri;
        this.endpoint = endpoint;
        this.graphs = graphs;
        this.dereferenceUri = null;
        this.sources = sources;
    }

    public RDFUnitConfiguration(String uri, String endpoint, java.util.Collection<String> graphs, String[] sourcePrefixes) {
        this(uri, endpoint, graphs, SchemaService.getSourceList(null, Arrays.asList(sourcePrefixes)));
    }

    public RDFUnitConfiguration(String uri, String endpoint, java.util.Collection<String> graphs, String sourcePrefixes) {
        this(uri, endpoint, graphs, sourcePrefixes.split(","));
    }

    public RDFUnitConfiguration(String uri, String endpoint, java.util.Collection<String> graphs, java.util.Collection<SchemaSource> sources) {
        this(CacheUtils.getAutoPrefixForURI(uri), uri, endpoint, graphs, sources);
    }

    /* For dereference source */
    public RDFUnitConfiguration(String prefix, String uri, String dereferenceUri, java.util.Collection<SchemaSource> sources) {
        this.prefix = prefix;
        this.uri = uri;
        this.endpoint = null;
        this.graphs = new ArrayList<String>();
        this.dereferenceUri = dereferenceUri;
        this.sources = sources;
    }

    public RDFUnitConfiguration(String uri, String location, java.util.Collection<SchemaSource> sources) {
        this(CacheUtils.getAutoPrefixForURI(uri), uri, location, sources);
    }

    // TODO change it back to Dateset after refactoring of Sources
    public Source getDatasetSource() {
        if ((endpoint == null || endpoint.equals(""))) {
            return new DumpSource(prefix, uri, DataReaderFactory.createFileOrDereferenceTripleReader(dereferenceUri), sources);
        } else {
            return new DatasetSource(prefix, uri, endpoint, graphs, sources);
        }
    }
}
