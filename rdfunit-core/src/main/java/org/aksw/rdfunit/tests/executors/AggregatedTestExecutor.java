package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.QueryGenerationFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.AggregatedTestCaseResult;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.Arrays;
import java.util.Collection;

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
        return Arrays.<TestCaseResult>asList(new AggregatedTestCaseResult(testCase, total, prevalence));
    }

    private int getCountNumber(QueryExecutionFactory model, Query query, String var) {

        assert (query != null);
        assert (var != null);

        int result = 0;
        QueryExecution qe = null;
        try {
            qe = model.createQueryExecution(query);
            ResultSet results = qe.execSelect();

            if (results != null && results.hasNext()) {
                QuerySolution qs = results.next();
                result = qs.get(var).asLiteral().getInt();
            }
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return result;

    }
}
