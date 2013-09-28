package org.aksw.databugger.sources;


import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * User: Dimitris Kontokostas
 * Abstract class for a data source. A source can be various things like a dataset, a vocabulary or an application
 * Date: 9/16/13 1:15 PM
 */

public abstract class Source {
    protected static final Logger log = LoggerFactory.getLogger(Source.class);

    private final String uri;
    private QueryExecutionFactory queryFactory;
    private String baseCacheFolder = "";

    public Source(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public abstract TestAppliesTo getSourceType();

    protected abstract QueryExecutionFactory initQueryFactory();

    public QueryExecutionFactory getExecutionFactory() {
        // TODO not thread safe but minor
        if (queryFactory == null)
            queryFactory = initQueryFactory();
        return queryFactory;
    }

    public String getTestFile(){
        return getBaseCacheFolder() + getCacheFolder() + "tests." + getSourceType().name() + ".ttl";
    }
    public String getCacheFile(){
        return getBaseCacheFolder() + getCacheFolder() + "cache." + getSourceType().name() + ".ttl";
    }

    protected String getCacheFolder(){
        String retVal = null;
        try {
            URI tmp = new URI(getUri());
            String host = tmp.getHost();
            String path = tmp.getPath();
            retVal = host + path + "/";
        } catch (Exception e) {
            // TODO handle exception
        }

        return retVal;
    }

    protected String getBaseCacheFolder() {
        return baseCacheFolder;
    }

    public void setBaseCacheFolder(String baseCacheFolder) {
        this.baseCacheFolder = baseCacheFolder;
    }
}
