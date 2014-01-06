package org.aksw.databugger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.Utils.PatternUtils;
import org.aksw.databugger.Utils.TestUtils;
import org.aksw.databugger.exceptions.TripleReaderException;
import org.aksw.databugger.io.TripleReader;
import org.aksw.databugger.patterns.Pattern;
import org.aksw.databugger.services.PatternService;
import org.aksw.databugger.services.PrefixService;
import org.aksw.databugger.tests.TestAutoGenerator;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Main Class
 * Created: 9/20/13 5:59 PM
 */
public class Databugger {


    private static Logger log = LoggerFactory.getLogger(Databugger.class);
    private QueryExecutionFactory patternQueryFactory;
    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<TestAutoGenerator> autoGenerators = new ArrayList<TestAutoGenerator>();

    public Databugger() {}

    public void initPatternsAndGenerators(TripleReader patterReader, TripleReader testGeneratorReader ) throws TripleReaderException {
        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patterReader.read(patternModel);
            testGeneratorReader.read(patternModel);
        } catch (TripleReaderException e) {
            throw new TripleReaderException(e.getMessage());
        }
        patternModel.setNsPrefixes(PrefixService.getPrefixMap());
        this.patternQueryFactory = new QueryExecutionFactoryModel(patternModel);
        this.patterns = getPatterns();

        // Update pattern service
        for (Pattern pattern : patterns) {
            PatternService.addPattern(pattern.getId(), pattern);
        }

        this.autoGenerators = getAutoGenerators();
    }

    public List<Pattern> getPatterns() {
        return PatternUtils.instantiatePatternsFromModel(patternQueryFactory);
    }

    public List<TestAutoGenerator> getAutoGenerators() {
        return TestUtils.instantiateTestGeneratorsFromModel(patternQueryFactory);
    }


}
