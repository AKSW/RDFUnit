package org.aksw.rdfunit;

import org.aksw.rdfunit.Utils.CacheUtils;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.DatasetSource;
import org.aksw.rdfunit.sources.DumpSource;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;

import java.util.Arrays;
import java.util.List;

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
    private final String graphs;

    private final List<SchemaSource> sources;


    public RDFUnitConfiguration(String prefix, String uri, String endpoint, String graphs, List<SchemaSource> sources) {
        this.prefix = prefix;
        this.uri = uri;
        this.endpoint = endpoint;
        this.graphs = graphs;
        this.sources = sources;
    }

    public RDFUnitConfiguration(String uri, String endpoint, String graphs, List<SchemaSource> sources) {
        this(CacheUtils.getAutoPrefixForURI(uri), uri, endpoint, graphs, sources);
    }

    public RDFUnitConfiguration(String uri, String endpoint, String graphs, String[] sourcePrefixes) {
        this(uri, endpoint, graphs, SchemaService.getSourceList(null, Arrays.asList(sourcePrefixes)));
    }

    public RDFUnitConfiguration(String uri, String endpoint, String graphs, String sourcePrefixes) {
        this(uri, endpoint, graphs, sourcePrefixes.split(","));
    }

    // TODO change it back to Dateset after refactoring of Sources
    public Source getDatasetSource() {
        if ((endpoint == null || endpoint.equals("")) && (graphs == null || graphs.equals(""))) {
            return new DumpSource(prefix, uri, sources);
        }
        else {
            return new DatasetSource(prefix, uri, endpoint, graphs, sources);
        }
    }
}
