package org.aksw.rdfunit.sources;


import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * <p>Abstract Source class.</p>
 *
 * @author Dimitris Kontokostas
 *         Abstract class for a data source. A source can be various things like a dataset, a vocabulary or an application
 *         Date: 9/16/13 1:15 PM
 * @version $Id: $Id
 */
public abstract class Source implements Comparable<Source> {
    /** Constant <code>log</code> */
    protected static final Logger log = LoggerFactory.getLogger(Source.class);

    private final String prefix;
    private final String uri;
    private final Set<SchemaSource> referencesSchemata;

    private QueryExecutionFactory queryFactory = null;

    /**
     * <p>Constructor for Source.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public Source(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
        this.referencesSchemata = new LinkedHashSet<>();
    }

    /**
     * <p>Constructor for Source.</p>
     *
     * @param source a {@link org.aksw.rdfunit.sources.Source} object.
     */
    public Source(Source source) {
        this(source.getPrefix(), source.getUri());
        this.referencesSchemata.addAll(source.getReferencesSchemata());
    }

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * <p>Getter for the field <code>uri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUri() {
        return uri;
    }

    /**
     * <p>getSourceType.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.TestAppliesTo} object.
     */
    public abstract TestAppliesTo getSourceType();

    /**
     * <p>initQueryFactory.</p>
     *
     * @return a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    protected abstract QueryExecutionFactory initQueryFactory();

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Source)) return false;

        Source source = (Source) o;

        return uri.equals(source.uri);

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    /**
     * <p>getExecutionFactory.</p>
     *
     * @return a {@link org.aksw.jena_sparql_api.core.QueryExecutionFactory} object.
     */
    public QueryExecutionFactory getExecutionFactory() {
        // TODO not thread safe but minor
        if (queryFactory == null) {
            queryFactory = initQueryFactory();
        }
        return queryFactory;
    }

    /**
     * <p>Getter for the field <code>referencesSchemata</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<SchemaSource> getReferencesSchemata() {
        return referencesSchemata;
    }

    /**
     * <p>addReferencesSchemata.</p>
     *
     * @param schemata a {@link java.util.Collection} object.
     */
    public void addReferencesSchemata(Collection<SchemaSource> schemata) {
        this.referencesSchemata.addAll(schemata);
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Source o) {
        if (o == null) {
            return -1;
        }

        return this.getUri().compareTo(o.getUri());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getPrefix() + " (" + getUri() + ")";
    }
}
