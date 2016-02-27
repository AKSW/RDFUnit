package org.aksw.rdfunit.sources;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

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
public class DumpTestSource extends AbstractTestSource implements TestSource {

    private final RdfReader dumpReader;

    private final OntModel dumpModel;

    DumpTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig, Collection<SchemaSource> referenceSchemata, RdfReader dumpReader, OntModel model) {
        super(sourceConfig, queryingConfig, referenceSchemata);
        this.dumpReader = checkNotNull(dumpReader);
        dumpModel = checkNotNull(model);
    }

    DumpTestSource(SourceConfig sourceConfig, QueryingConfig queryingConfig, Collection<SchemaSource> referenceSchemata, RdfReader dumpReader) {
        this(sourceConfig, queryingConfig, referenceSchemata, dumpReader, ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel()));  //OntModelSpec.RDFS_MEM_RDFS_INF
    }

    DumpTestSource(DumpTestSource dumpTestSource, Collection<SchemaSource> referenceSchemata) {
        this(dumpTestSource.sourceConfig, dumpTestSource.queryingConfig, referenceSchemata, dumpTestSource.dumpReader, dumpTestSource.dumpModel);
    }

    /** {@inheritDoc} */
    @Override
    protected QueryExecutionFactory initQueryFactory() {

        // When we load the referenced schemata we do rdfs reasoning to avoid false errors
        // Many ontologies skip some domain / range statements and this way we add them
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, ModelFactory.createDefaultModel());
        try {

            // load the data only when the model is empty in case it is initialized with the "copy constructor"
            // This sppeds up the process on very big in-memory datasets
            if (dumpModel.isEmpty()) {
                // load the data only when the model is empty in case it is initialized with the "copy constructor"
                dumpReader.read(dumpModel);
            }

            //if (dumpModel.isEmpty()) {
            //    throw new IllegalArgumentException("Dump is empty");
            //}
            //Load all the related ontologies as well (for more consistent querying
            for (SchemaSource src : getReferencesSchemata()) {
                ontModel.add(src.getModel());
            }
            // Here we add the ontologies in the dump mode
            // Note that the ontologies have reasoning enabled but not the dump source
            dumpModel.add(ontModel);
        } catch (Exception e) {
            LOGGER.error("Cannot read dump URI: " + getUri() + " Reason: " + e.getMessage());
            throw new IllegalArgumentException("Cannot read dump URI: " + getUri() + " Reason: " + e.getMessage(), e);
        }
        return masqueradeQEF(new QueryExecutionFactoryModel(dumpModel), this);
    }


}
