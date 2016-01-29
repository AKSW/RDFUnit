package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.*;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.tests.TestCaseValidator;
import org.aksw.rdfunit.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Instantiates TestCases based on a test generator and a schema
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 1:23 PM
 * @version $Id: $Id
 */
public class TestGeneratorTCInstantiator {

    private static final Logger log = LoggerFactory.getLogger(TestGeneratorTCInstantiator.class);


    private final ImmutableList<TestGenerator> testGenerators;
    private final SchemaSource source;

    /**
     * <p>Constructor for TestGeneratorTCInstantiator.</p>
     *
     * @param testGenerators a {@link java.util.Collection} object.
     * @param source a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public TestGeneratorTCInstantiator(Collection<TestGenerator> testGenerators, SchemaSource source) {
        this.testGenerators = ImmutableList.copyOf(testGenerators);
        this.source = source;
    }

    /**
     * <p>generate.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<TestCase> generate() {


        Collection<TestCase> tests = new ArrayList<>();

        for (TestGenerator tg: testGenerators) {
                    Pattern tgPattern = tg.getPattern();

                        Query q = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + tg.getQuery());
            QueryExecution qe = new QueryExecutionFactoryModel(source.getModel()).createQueryExecution(q);
                        ResultSet rs = qe.execSelect();

                        while (rs.hasNext()) {
                            QuerySolution row = rs.next();

                            Collection<Binding> bindings = new ArrayList<>();
                            Collection<String> references = new ArrayList<>();
                            String description;

                            for (PatternParameter p : tgPattern.getParameters()) {
                                if (row.contains(p.getId())) {
                                    RDFNode n = row.get(p.getId());
                                    Binding b;
                                    try {
                                        b = new Binding(p, n);
                                    } catch (Exception e) {
                                        log.error("Non valid binding for parameter {} in AutoGenerator: {}", p.getId(), tg.getUri(), e);
                                        continue;
                                    }
                                    bindings.add(b);
                                    if (n.isResource() && !"loglevel".equalsIgnoreCase(p.getId())) {
                                        references.add(n.toString().trim().replace(" ", ""));
                                    }
                                } else {
                                    log.error("Not bindings for parameter {} in AutoGenerator: {}", p.getId(), tg.getUri());
                                    break;
                                }
                            }
                            if (bindings.size() != tg.getPattern().getParameters().size()) {
                                log.error("Bindings for pattern {} do not match in AutoGenerator: {}", tgPattern.getId(), tg.getUri());
                                continue;
                            }

                            if (row.get("DESCRIPTION") != null) {
                                description = row.get("DESCRIPTION").toString();
                            } else {
                                log.error("No ?DESCRIPTION variable found in AutoGenerator: {}", tg.getUri());
                                continue;
                            }



                            Collection<ResultAnnotation> patternBindedAnnotations = tgPattern.getBindedAnnotations(bindings);
                            patternBindedAnnotations.addAll(tg.getAnnotations());
                            Resource tcResource = ResourceFactory.createResource(TestUtils.generateTestURI(source.getPrefix(), tgPattern, bindings, tg.getUri()));
                            PatternBasedTestCaseImpl tc = new PatternBasedTestCaseImpl(
                                    tcResource,
                                    new TestCaseAnnotation(
                                            tcResource, TestGenerationType.AutoGenerated,
                                            tg.getUri(),
                                            source.getSourceType(),
                                            source.getUri(),
                                            references,
                                            description,
                                            null,
                                            patternBindedAnnotations),
                                    tgPattern,
                                    bindings
                            );
                            new TestCaseValidator(tc).validate();
                            tests.add(tc);


                        }
                    }
        return tests;
    }
}
