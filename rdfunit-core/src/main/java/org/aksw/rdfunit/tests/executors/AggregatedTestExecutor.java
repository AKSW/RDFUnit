package org.aksw.rdfunit.tests.executors;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.impl.results.AggregatedTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.SparqlUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.Collection;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test Executor that extends StatusExecutor and in addition reports error counts and prevalence for every test case
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 4:05 PM
 * @version $Id: $Id
 */
public class AggregatedTestExecutor extends TestExecutor {

    /**
     * Instantiates a new AggregatedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public AggregatedTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    /** {@inheritDoc} */
    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {
        int total = -1, prevalence = -1;

        try {
            Query prevalenceQuery = testCase.getSparqlPrevalenceQuery();
            if (prevalenceQuery != null) {
                prevalence = getCountNumber(testSource.getExecutionFactory(), testCase.getSparqlPrevalenceQuery(), "total");
            }
        } catch (QueryExceptionHTTP e) {
            if (SparqlUtils.checkStatusForTimeout(e)) {
                prevalence = -1;
            } else {
                prevalence = -2;
            }
        }

        if (prevalence != 0) {
            // if prevalence !=0 calculate total
            try {
                total = getCountNumber(testSource.getExecutionFactory(), queryGenerationFactory.getSparqlQuery(testCase), "total");
            } catch (QueryExceptionHTTP e) {
                if (SparqlUtils.checkStatusForTimeout(e)) {
                    total = -1;
                } else {
                    total = -2;
                }
            }
        } else {
            // else total will be 0 anyway
            total = 0;
        }

        // No need to throw exception here, class supports status
        return Collections.singletonList(new AggregatedTestCaseResultImpl(testCase, total, prevalence));
    }

    private int getCountNumber(QueryExecutionFactory model, Query query, String var) {

        checkNotNull(query);
        checkNotNull(var);

        int result = 0;
        try ( QueryExecution qe = model.createQueryExecution(query) ) {

            ResultSet results = qe.execSelect();

            if (results != null && results.hasNext()) {
                QuerySolution qs = results.next();
                result = qs.get(var).asLiteral().getInt();
            }
        }

        return result;

    }
}
