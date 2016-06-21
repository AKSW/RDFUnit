package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory that returns select queries and besides
 * SELECT ?this it adds all variable annotations
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:02 PM
 * @version $Id: $Id
 */
public class QueryGenerationExtendedSelectFactory implements QueryGenerationFactory {

    private static final String SELECT_DISTINCT_RESOURCE = " SELECT DISTINCT ?this ";

    private static final String RESOURCE_VAR = "?this";

    private static final String WHERE_CLAUSE = " WHERE ";

    private static final String ORDER_BY_RESOURCE_ASC = "  ORDER BY ASC(?this) ";

    /** {@inheritDoc} */
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {

        StringBuilder sb = new StringBuilder();

        sb.append(PrefixNSService.getSparqlPrefixDecl());
        sb.append(SELECT_DISTINCT_RESOURCE);

        Set<String> existingVariables = new HashSet<>();
        existingVariables.add(RESOURCE_VAR);

        // Add all defined variables in the query
        for (ResultAnnotation annotation : testCase.getVariableAnnotations()) {
            String value = annotation.getAnnotationVarName().get().trim();

            // if variable is not redefined don't add it again
            // This is needed if the same variable takes part in different annotations
            if (!existingVariables.contains(value)) {
                sb.append(" ?");
                sb.append(value);
                sb.append(' ');

                existingVariables.add(value);
            }
        }
        sb.append(WHERE_CLAUSE);
        sb.append(testCase.getSparqlWhere());
        sb.append(ORDER_BY_RESOURCE_ASC);
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Query getSparqlQuery(TestCase testCase) {
        return QueryFactory.create(this.getSparqlQueryAsString(testCase));
    }
}
