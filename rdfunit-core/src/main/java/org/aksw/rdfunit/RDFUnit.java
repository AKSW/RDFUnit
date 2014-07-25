package org.aksw.rdfunit;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.PatternUtils;
import org.aksw.rdfunit.Utils.TestUtils;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.io.RDFReader;
import org.aksw.rdfunit.patterns.Pattern;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.TestAutoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Main Class
 * @since 9/20/13 5:59 PM
 */
public class RDFUnit {


    private static Logger log = LoggerFactory.getLogger(RDFUnit.class);
    private QueryExecutionFactory patternQueryFactory;
    private Collection<Pattern> patterns = new ArrayList<>();
    private Collection<TestAutoGenerator> autoGenerators = new ArrayList<>();

    public RDFUnit() {
    }

    public void initPatternsAndGenerators(RDFReader patterReader, RDFReader testGeneratorReader) throws TripleReaderException {
        Model patternModel = ModelFactory.createDefaultModel();
        try {
            patterReader.read(patternModel);
            testGeneratorReader.read(patternModel);
        } catch (TripleReaderException e) {
            throw new TripleReaderException(e.getMessage(), e);
        }

        PrefixNSService.setNSPrefixesInModel(patternModel);
        this.patternQueryFactory = new QueryExecutionFactoryModel(patternModel);
        this.patterns = getPatterns();

        // Update pattern service
        for (Pattern pattern : patterns) {
            PatternService.addPattern(pattern.getId(), pattern);
        }

        this.autoGenerators = getAutoGenerators();
    }

    public Collection<Pattern> getPatterns() {
        return PatternUtils.instantiatePatternsFromModel(patternQueryFactory);
    }

    public Collection<TestAutoGenerator> getAutoGenerators() {
        return TestUtils.instantiateTestGeneratorsFromModel(patternQueryFactory);
    }


}
