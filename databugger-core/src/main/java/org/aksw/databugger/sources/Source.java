package org.aksw.databugger.sources;


import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Abstract class for a data source. A source can be various things like a dataset, a vocabulary or an application
 * Date: 9/16/13 1:15 PM
 */

public abstract class Source {
    protected static final Logger log = LoggerFactory.getLogger(Source.class);

    private final String prefix;
    private final String uri;
    private final List<SchemaSource> referencesSchemata;

    private QueryExecutionFactory queryFactory;
    private String baseCacheFolder = "";

    public Source(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
        this.referencesSchemata = new ArrayList<SchemaSource>();
    }

    public String getPrefix() {
        return prefix;
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

    public String getTestFile() {
        return getFile("tests", getSourceType().name());
    }

    public String getTestFileManual() {
        return getFile("tests", "Manual");
    }

    public String getCacheFile() {
        return getFile("cache", getSourceType().name());
    }

    private String getFile(String type, String sourceType) {
        return getBaseCacheFolder() + getCacheFolder() + prefix + "." + type + "." + sourceType + ".ttl";
    }

    protected String getCacheFolder() {
        String retVal = null;
        try {
            URI tmp = new URI(getUri());
            String host = tmp.getHost();
            String path = tmp.getPath();
            retVal = getSourceType().name() + "/" + host + path + "/";
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
        for (Source src : getReferencesSchemata()) {
            src.setBaseCacheFolder(baseCacheFolder);
        }
    }

    public List<SchemaSource> getReferencesSchemata() {
        return referencesSchemata;
    }

    public void addReferencesSchemata(List<SchemaSource> schemata) {
        this.referencesSchemata.addAll(schemata);
    }
}
