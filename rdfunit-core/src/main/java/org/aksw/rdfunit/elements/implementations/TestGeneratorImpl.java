package org.aksw.rdfunit.elements.implementations;

import com.google.common.base.Optional;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.elements.interfaces.*;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.exceptions.BindingException;
import org.aksw.rdfunit.exceptions.TestCaseInstantiationException;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.tests.Binding;
import org.aksw.rdfunit.tests.TestCaseAnnotation;
import org.aksw.rdfunit.tests.TestCaseValidator;
import org.aksw.rdfunit.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * <p>TestGenerator class.</p>
 *
 * @author Dimitris Kontokostas
 *         Takes a sparqlPattern and a SPARQL query and based on the data
 *         returned from that query we generate test cases
 * @since 9/20/13 2:48 PM
 * @version $Id: $Id
 */
public final class TestGeneratorImpl implements TestGenerator {
    private static final Logger log = LoggerFactory.getLogger(TestGeneratorImpl.class);

    private final Resource element;
    private final String description;
    private final String query;
    private final Pattern pattern;
    private final Collection<ResultAnnotation> generatorAnnotations;


    /**
     * <p>Constructor for TestGenerator.</p>
     *
     * @param element
     * @param description a {@link java.lang.String} object.
     * @param query a {@link java.lang.String} object.
     * @param pattern a {@link Pattern} object.
     * @param generatorAnnotations a {@link java.util.Collection} object.
     */
    private TestGeneratorImpl(Resource element, String description, String query, Pattern pattern, Collection<ResultAnnotation> generatorAnnotations) {

        this.element = checkNotNull(element);
        String tagName = element.getLocalName();

        this.description = checkNotNull(description, "Description in %s should not be null", tagName);
        this.query = checkNotNull(query, "Query in %s should not be null", tagName);
        checkState(!query.trim().isEmpty(), "Query in %s should not be empty", tagName);
        this.pattern = checkNotNull(pattern, "Pattern in %s should not be null", tagName);
        this.generatorAnnotations = checkNotNull(generatorAnnotations);
    }

    /**
     * <p>createTAG.</p>
     *
     * @param element a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param description a {@link java.lang.String} object.
     * @param query a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.elements.interfaces.Pattern} object.
     * @param generatorAnnotations a {@link java.util.Collection} object.
     * @return a {@link org.aksw.rdfunit.elements.interfaces.TestGenerator} object.
     */
    public static TestGenerator createTAG(Resource element, String description, String query, Pattern pattern, Collection<ResultAnnotation> generatorAnnotations) {
        return new TestGeneratorImpl(element,description,query,pattern,generatorAnnotations);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(element);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid() {
        Query q;
        if (pattern == null) {
            log.error("{} : Pattern {} does not exist", getTAGUri(), getTAGPattern());
            return false;
        }
        try {
            q = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getTAGQuery());
        } catch (Exception e) {
            log.error("{} Cannot parse query:\n{}", getTAGUri(), PrefixNSService.getSparqlPrefixDecl() + getTAGQuery(), e);
            return false;
        }

        Collection<Var> sv = q.getProjectVars();
        if (sv.size() != pattern.getParameters().size() + 1) {
            log.error("{} Select variables are different than Pattern parameters", getTAGUri());
            return false;
        }


        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<TestCase> generate(SchemaSource source) {
        Collection<TestCase> tests = new ArrayList<>();

        Query q = QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getTAGQuery());
        QueryExecution qe = new QueryExecutionFactoryModel(source.getModel()).createQueryExecution(q);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution row = rs.next();

            Collection<Binding> bindings = new ArrayList<>();
            Collection<String> references = new ArrayList<>();
            String description;

            for (PatternParameter p : pattern.getParameters()) {
                if (row.contains(p.getId())) {
                    RDFNode n = row.get(p.getId());
                    Binding b;
                    try {
                        b = new Binding(p, n);
                    } catch (BindingException e) {
                        log.error("Non valid binding for parameter {} in AutoGenerator: {}", p.getId(), this.getTAGUri(), e);
                        continue;
                    }
                    bindings.add(b);
                    if (n.isResource() && !"loglevel".equalsIgnoreCase(p.getId())) {
                        references.add(n.toString().trim().replace(" ", ""));
                    }
                } else {
                    log.error("Not bindings for parameter {} in AutoGenerator: {}", p.getId(), this.getTAGUri());
                    break;
                }
            }
            if (bindings.size() != getTAGPattern().getParameters().size()) {
                log.error("Bindings for pattern {} do not match in AutoGenerator: {}", pattern.getId(), this.getTAGUri());
                continue;
            }

            if (row.get("DESCRIPTION") != null) {
                description = row.get("DESCRIPTION").toString();
            } else {
                log.error("No ?DESCRIPTION variable found in AutoGenerator: {}", this.getTAGUri());
                continue;
            }


            try {
                Collection<ResultAnnotation> patternBindedAnnotations = pattern.getBindedAnnotations(bindings);
                patternBindedAnnotations.addAll(generatorAnnotations);
                PatternBasedTestCaseImpl tc = new PatternBasedTestCaseImpl(
                        TestUtils.generateTestURI(source.getPrefix(), getTAGPattern(), bindings, getTAGUri()),
                        new TestCaseAnnotation(
                                TestGenerationType.AutoGenerated,
                                this.getTAGUri(),
                                source.getSourceType(),
                                source.getUri(),
                                references,
                                description,
                                null,
                                patternBindedAnnotations),
                        pattern,
                        bindings
                );
                new TestCaseValidator(tc).validate();
                tests.add(tc);
            } catch (TestCaseInstantiationException e) {
                log.error(e.getMessage(), e);
            }

        }
        return tests;
    }

    /** {@inheritDoc} */
    @Override
    public String getTAGUri() {
        return element.getURI();
    }

    /** {@inheritDoc} */
    @Override
    public String getTAGDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public String getTAGQuery() {
        return query;
    }

    /** {@inheritDoc} */
    @Override
    public Pattern getTAGPattern() {
        return pattern;
    }

}
