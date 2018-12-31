package org.aksw.rdfunit.tests.query_generation;

import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.utils.CommonNames;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;

import java.util.Optional;

import static org.aksw.rdfunit.tests.query_generation.QueryGenerationUtils.getPrefixDeclarations;

/**
 * Factory that returns simple select queries
 *
 * @author Dimitris Kontokostas
 * @since 7/25/14 10:02 PM

 */
public class QueryGenerationSelectFactory implements QueryGenerationFactory {

    private static final String SELECT_CLAUSE = " SELECT ?this ";


    @Override
    public String getSparqlQueryAsString(TestCase testCase) {        StringBuilder sb = new StringBuilder();

        sb.append(getPrefixDeclarations(testCase));
        sb.append(SELECT_CLAUSE);

        // Add all defined variables in the query
        Optional<String> focusNode = testCase.getVariableAnnotations().stream()
                .filter(va -> va.getAnnotationProperty().equals(SHACL.focusNode))
                .map(ResultAnnotation::getAnnotationVarName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(f -> ! f.equals(CommonNames.This))
                .findFirst();
        focusNode.ifPresent(focus -> sb.append("?").append(focus));

        sb.append(" WHERE ");
        sb.append(testCase.getSparqlWhere());
        return sb.toString();
    }


    @Override
    public Query getSparqlQuery(TestCase testCase) {
        String query = this.getSparqlQueryAsString(testCase);
        try {
            return QueryFactory.create(query);
        } catch (QueryParseException e) {
            throw new IllegalArgumentException("Illegal query: \n" + query, e);
        }
    }
}
