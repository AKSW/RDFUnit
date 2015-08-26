package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/26/15 12:55 PM
 */
public class AbstractTestSourceTest {

    private AbstractTestSource abstractTestSource;
    private QueryingConfig queryingConfig;
    private SourceConfig sourceConfig;

    private static final long TESTCacheTTL = 111;
    private static final long TESTQueryDelay = 222;
    private static final long TESTTestqueryLimit = 333;
    private static final long TESTPagination = 444;

    private static final String TESTSourcePrefix = "prefix";
    private static final String TESTSourceURI = "http://example.com";
    private static final Collection<SchemaSource> TESTReferenceSchemata = Collections.emptyList();

    @Before
    public void setUp() throws Exception {

        queryingConfig = QueryingConfig.create(TESTCacheTTL, TESTQueryDelay, TESTTestqueryLimit, TESTPagination);
        sourceConfig = new SourceConfig(TESTSourcePrefix, TESTSourceURI);

        abstractTestSource = new AbstractTestSource(sourceConfig, queryingConfig, TESTReferenceSchemata) {
            @Override
            protected QueryExecutionFactory initQueryFactory() {
                throw new IllegalStateException();
            }
        };

    }

    @Test
    public void testGetQueryingConfig() throws Exception {
        assertThat(abstractTestSource.getQueryingConfig())
        .isEqualTo(queryingConfig);
    }

    @Test
    public void testGetReferencesSchemata() throws Exception {
        for (SchemaSource schemaSource: abstractTestSource.getReferencesSchemata()) {
            assertThat(TESTReferenceSchemata.contains(schemaSource));
        }
    }

    @Test
    public void testGetPrefix() throws Exception {
        assertThat(abstractTestSource.getPrefix())
                .isEqualTo(sourceConfig.getPrefix());
    }

    @Test
    public void testGetUri() throws Exception {
        assertThat(abstractTestSource.getUri())
                .isEqualTo(sourceConfig.getUri());
    }

    @Test
    public void testGetSourceType() throws Exception {
        assertThat(abstractTestSource.getSourceType())
                .isEqualTo(TestAppliesTo.Dataset);

    }

    @Test
    public void testGetCacheTTL() throws Exception {
        assertThat(abstractTestSource.getCacheTTL())
                .isEqualTo(queryingConfig.getCacheTTL());

    }

    @Test
    public void testGetQueryDelay() throws Exception {
        assertThat(abstractTestSource.getQueryDelay())
                .isEqualTo(queryingConfig.getQueryDelay());
    }

    @Test
    public void testGetQueryLimit() throws Exception {
        assertThat(abstractTestSource.getQueryLimit())
                .isEqualTo(queryingConfig.getQueryLimit());
    }

    @Test
    public void testGetPagination() throws Exception {
        assertThat(abstractTestSource.getPagination())
                .isEqualTo(queryingConfig.getPagination());
    }
}