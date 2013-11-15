package org.aksw.databugger;

import org.aksw.databugger.Utils.CacheUtils;
import org.aksw.databugger.services.SchemaService;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.SchemaSource;

import java.util.Arrays;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Holds a configuration for a complete test
 * Created: 11/15/13 11:50 AM
 */
public class DatabuggerConfiguration {

    private final String prefix;
    private final String uri;
    private final String endpoint;

    // multiple graphs separated with '|'
    private final String graphs;

    private final List<SchemaSource> sources;

    private final DatasetSource datasetSource;

    public DatabuggerConfiguration(String prefix, String uri, String endpoint, String graphs, List<SchemaSource> sources) {
        this.prefix = prefix;
        this.uri = uri;
        this.endpoint = endpoint;
        this.graphs = graphs;
        this.sources = sources;

        this.datasetSource = new DatasetSource(prefix, uri, endpoint, graphs, sources);
    }

    public DatabuggerConfiguration(String uri, String endpoint, String graphs, List<SchemaSource> sources) {
        this(CacheUtils.getAutoPrefixForURI(uri), uri, endpoint, graphs, sources);
    }

    public DatabuggerConfiguration(String uri, String endpoint, String graphs, String[] sourcePrefixes) {
        this(uri, endpoint, graphs, SchemaService.getSourceList(null, Arrays.asList(sourcePrefixes)));
    }

    public DatabuggerConfiguration(String uri, String endpoint, String graphs, String sourcePrefixes) {
        this(uri, endpoint, graphs, sourcePrefixes.split(","));
    }

    public DatasetSource getDatasetSource() {
        return datasetSource;
    }
}
