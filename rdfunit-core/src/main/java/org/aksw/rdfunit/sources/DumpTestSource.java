package org.aksw.rdfunit.sources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;

import java.util.Collection;

/**
 * Defines a source based on an RDF Dump
 * This can be any type of dump (ttl, nt, rdfa, rdf, etc)
 * This has to be more sophisticated in the end (e.g. read zippped files) but there is always time to improve ;)
 * TODO refactor sources and make this a subclass of DatasetSource
 *
 * @author Dimitris Kontokostas
 * @since 2/6/14 9:32 AM
 * @version $Id: $Id
 */
public class DumpTestSource extends TestSource {

    private final RDFReader dumpReader;

    private final OntModel dumpModel;

    /**
     * <p>Constructor for DumpTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     */
    public DumpTestSource(String prefix, String uri) {
        this(prefix, uri, RDFReaderFactory.createDereferenceReader(uri), null);
    }

    /**
     * <p>Constructor for DumpTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param location a {@link java.lang.String} object.
     */
    public DumpTestSource(String prefix, String uri, String location) {
        this(prefix, uri, location, null);
    }

    /**
     * <p>Constructor for DumpTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param location a {@link java.lang.String} object.
     * @param schemata a {@link java.util.Collection} object.
     */
    public DumpTestSource(String prefix, String uri, String location, Collection<SchemaSource> schemata) {
        this(prefix, uri, RDFReaderFactory.createDereferenceReader(location), schemata);
    }

    /**
     * Instantiates a new Test source along with a collection os schemata.
     *
     * @param source the source
     * @param referencesSchemata the references schemata
     */
    public DumpTestSource(DumpTestSource source, Collection<SchemaSource> referencesSchemata ) {
        super(source);
        this.addReferencesSchemata(referencesSchemata);

        this.dumpReader = source.dumpReader;
        this.dumpModel  = source.dumpModel;

        this.cacheTTL = source.cacheTTL;
        this.queryDelay = source.queryDelay;
        this.queryLimit = source.queryLimit;
        this.pagination = source.pagination;
    }

    /**
     * <p>Constructor for DumpTestSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param dumpReader a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     * @param schemata a {@link java.util.Collection} object.
     */
    public DumpTestSource(String prefix, String uri, RDFReader dumpReader, Collection<SchemaSource> schemata) {
        super(prefix, uri);

        cacheTTL = 0; // defaults to 0 unless overridden
        queryDelay = 0; // defaults to 0 unless overridden
        queryLimit = 0; // defaults to 0 unless overridden
        pagination = 0; // defaults to 0 unless overridden

        this.dumpReader = dumpReader;
        if (schemata != null) {
            addReferencesSchemata(schemata);
        }

        dumpModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Dataset;
    }

    /** {@inheritDoc} */
    @Override
    protected QueryExecutionFactory initQueryFactory() {

        // When we load the referenced schemata we do rdfs reasoning to avoid false errors
        // Many ontologies skip some domain / range statements and this way we add them
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, ModelFactory.createDefaultModel());
        try {
            // Read & load the URI
            if (dumpModel.isEmpty()) {
                // load the data only when the model is empty in case it is initialized with the "copy constructor"
                dumpReader.read(dumpModel);
            }

            if (dumpModel.isEmpty()) {
                throw new IllegalArgumentException("Dump is empty");
            }
            //Load all the related ontologies as well (for more consistent querying
            for (Source src : getReferencesSchemata()) {
                QueryExecutionFactory qef = src.getExecutionFactory();
                if (qef instanceof QueryExecutionFactoryModel) {
                    ontModel.add(((QueryExecutionFactoryModel) qef).getModel());
                }
            }
            // Here we add the ontologies in the dump mode
            // Note that the ontologies have reasoning enabled but not the dump source
            dumpModel.add(ontModel);
        } catch (Exception e) {
            log.error("Cannot read dump URI: " + getUri() + " Reason: " + e.getMessage());
            throw new IllegalArgumentException("Cannot read dump URI: " + getUri() + " Reason: " + e.getMessage(), e);
        }
        return masqueradeQEF(new QueryExecutionFactoryModel(dumpModel));
    }
}
