package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.Utils.PatternUtils;
import org.aksw.databugger.Utils.TestUtils;
import org.aksw.databugger.coverage.TestCoverageEvaluator;
import org.aksw.databugger.exceptions.TripleReaderException;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.services.PatternService;
import org.aksw.databugger.services.PrefixService;
import org.aksw.databugger.services.SchemaService;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.sources.SourceFactory;
import org.aksw.databugger.tests.TestAutoGenerator;
import org.aksw.databugger.tests.TestExecutor;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.databugger.tripleReaders.TripleReaderFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.apache.commons.cli.*;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {


    private static Logger log = LoggerFactory.getLogger(Databugger.class);
    QueryExecutionFactory patternQueryFactory;
    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

    public Databugger(String dataFolder) {
        this.patternQueryFactory = loadPatterns(dataFolder + "patterns.ttl", dataFolder + "testGenerators.ttl");
        this.patterns = getPatterns();

        // Update pattern service
        for (Pattern pattern : patterns) {
            PatternService.addPattern(pattern.getId(), pattern);
        }

        this.autoGenerators = getAutoGenerators();


    }

    public QueryExecutionFactory loadPatterns(String patf, String genf) {

        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patternModel.read(new FileInputStream(patf), null, "TURTLE");
            patternModel.read(new FileInputStream(genf), null, "TURTLE");
        } catch (Exception e) {
            log.error("patterns and generators files were not found in data folder");
            System.exit(1);
        }
        patternModel.setNsPrefixes(PrefixService.getPrefixMap());
        return new QueryExecutionFactoryModel(patternModel);
    }

    public List<Pattern> getPatterns() {
        return PatternUtils.instantiatePatternsFromModel(patternQueryFactory);
    }

    public List<TestAutoGenerator> getAutoGenerators() {
        return TestUtils.instantiateTestGeneratorsFromModel(patternQueryFactory);
    }


}
