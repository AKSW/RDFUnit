package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.results.ExtendedTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.aksw.rdfunit.utils.StringUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Extended Test Executor extends RLOG Executor but provides richer error metadata
 * TODO: At the moment this is partially
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 6:13 PM
 * @version $Id: $Id
 */
@Deprecated
public class ExtendedTestExecutor extends RLOGTestExecutor {

    /**
     * Instantiates a new ExtendedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ExtendedTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    /** {@inheritDoc} */
    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {

        Collection<TestCaseResult> testCaseResults = new ArrayList<>();
        PropertyValuePairSet.PropertyValuePairSetBuilder annotationSetBuilder = PropertyValuePairSet.builder();

        try ( QueryExecution qe = testSource.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase))){

            ResultSet results = qe.execSelect();

            ExtendedTestCaseResultImpl.Builder resultBuilder = null;
            String prevResource = "";

            while (results.hasNext()) {

                QuerySolution qs = results.next();

                String resource = qs.get("this").toString();
                if (qs.get("this").isLiteral()) {
                    resource = StringUtils.getHashFromString(resource);
                }
                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                // If resource != before
                // we add the previous result in the list
                if (!prevResource.equals(resource)) {
                    // The very first time we enter, result = null and we don't add any result
                    if (resultBuilder != null) {
                        testCaseResults.add(
                                resultBuilder
                                        .setResultAnnotations(annotationSetBuilder.build().getAnnotations())
                                        .build());
                    }

                    resultBuilder = new ExtendedTestCaseResultImpl.Builder(testCase.getTestURI(), logLevel, message, resource );

                    annotationSetBuilder = PropertyValuePairSet.builder(); // reset

                    // get static annotations for new test
                    for (ResultAnnotation resultAnnotation : testCase.getResultAnnotations()) {
                        // Get values
                        if (resultAnnotation.getAnnotationValue().isPresent()) {
                            annotationSetBuilder.annotation(PropertyValuePair.create(resultAnnotation.getAnnotationProperty(), resultAnnotation.getAnnotationValue().get()));
                        }
                    }
                }

                // result must be initialized by now
                checkNotNull(resultBuilder);

                // get annotations from the SPARQL query
                for (ResultAnnotation resultAnnotation : testCase.getResultAnnotations()) {
                    // Get the variable name
                    if (resultAnnotation.getAnnotationVarName().isPresent()) {
                        String variable = resultAnnotation.getAnnotationVarName().get().trim();
                        //If it exists, add it in the Set
                        if (qs.contains(variable)) {
                            annotationSetBuilder.annotation(PropertyValuePair.create(resultAnnotation.getAnnotationProperty(), qs.get(variable)));
                        }
                    }
                }
            }
            // Add last result (if query return any)
            if (resultBuilder != null) {
                testCaseResults.add(resultBuilder.setResultAnnotations(annotationSetBuilder.build().getAnnotations()).build());
            }
        } catch (QueryExceptionHTTP e) {
            checkQueryResultStatus(e);
        }

        return testCaseResults;

    }
}
