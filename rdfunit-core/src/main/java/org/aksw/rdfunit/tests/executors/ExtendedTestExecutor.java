package org.aksw.rdfunit.tests.executors;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.QueryGenerationFactory;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.results.ExtendedTestCaseResult;
import org.aksw.rdfunit.tests.results.ResultAnnotation;
import org.aksw.rdfunit.tests.results.TestCaseResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The Extended Test Executor extends RLOG Executor but provides richer error metadata
 * TODO: At the moment this is partially
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 6:13 PM
 */
public class ExtendedTestExecutor extends RLOGTestExecutor {

    /**
     * Instantiates a new ExtendedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ExtendedTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    @Override
    protected Collection<TestCaseResult> executeSingleTest(Source source, TestCase testCase) throws TestCaseExecutionException {

        Collection<TestCaseResult> testCaseResults = new ArrayList<>();

        QueryExecution qe = null;
        try {
            qe = source.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase));
            ResultSet results = qe.execSelect();

            ExtendedTestCaseResult result = null;
            String prevResource = "";

            while (results.hasNext()) {

                QuerySolution qs = results.next();

                String resource = qs.get("resource").toString();
                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                // If resource != before
                // we add the previous result in the list
                if (!prevResource.equals(resource)) {
                    // The very first time we enter, result = null and we don't add any result
                    if (result != null) {
                        testCaseResults.add(result);
                    }

                    result = new ExtendedTestCaseResult(testCase, resource, message, logLevel);
                }

                // result must be initialized by now
                assert result != null ;

                for (Map.Entry<ResultAnnotation, Set<RDFNode>> vaEntry: result.getVariableAnnotationsMap().entrySet()) {
                    // Get the variable name
                    String variable = vaEntry.getKey().getAnnotationValue().toString().trim().replace("?", "");
                    //If it exists, add it in the Set
                    if (qs.contains(variable)) {
                        vaEntry.getValue().add(qs.get(variable));
                    }
                }
            }
            // Add last result (if query return any)
            if (result != null) {
                testCaseResults.add(result);
            }
        } catch (QueryExceptionHTTP e) {
            checkQueryResultStatus(e);
        } finally {
            if (qe != null) {
                qe.close();
            }
        }

        return testCaseResults;

    }
}
