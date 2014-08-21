package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

/**
 * Factory that returns select queries and besides
 * SELECT ?resource it adds all variable annotations
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:02 PM
 */
public class QueryGenerationExtendedSelectFactory implements QueryGenerationFactory {
    @Override
    public String getSparqlQueryAsString(TestCase testCase) {

        StringBuilder sb = new StringBuilder();

        sb.append(PrefixNSService.getSparqlPrefixDecl());
        sb.append(" SELECT DISTINCT ?resource ");

        // Add all defined variables in the query
        for (ResultAnnotation annotation : testCase.getVariableAnnotations()) {
            String value = annotation.getAnnotationValue().toString().trim();

            // if resource is redefined don't add it again
            if (!value.equalsIgnoreCase("?resource")) {
                sb.append(" ");
                sb.append(value);
                sb.append(" ");
            }
        }
        sb.append("  WHERE ");
        sb.append(testCase.getSparqlWhere());
        return  sb.toString();
    }

    @Override
    public Query getSparqlQuery(TestCase testCase) {
        return QueryFactory.create(this.getSparqlQueryAsString(testCase));
    }
}
