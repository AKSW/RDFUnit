package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.ARQException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dimitris Kontokostas
 * @since 8/16/17
 */
@Value
public class QueryPrebinding {
    @NonNull private final String sparqlQuery;
    @NonNull private final Shape shape;

    public String applyBindings(Map<ComponentParameter, RDFNode> bindings) {

        String bindedQuery = sparqlQuery;
        if (shape.isPropertyShape()) {
            bindedQuery = bindedQuery.replaceAll(Pattern.quote("$PATH"), Matcher.quoteReplacement(shape.getPath().get().asSparqlPropertyPath()));
        }
        ParameterizedSparqlString query = new ParameterizedSparqlString(bindedQuery);

        try {
            for (Map.Entry<ComponentParameter, RDFNode> entry : bindings.entrySet()) {
                // get same value or formatted based on query
                RDFNode node = entry.getKey().getBindingValue(entry.getValue(), shape);

                if (entry.getKey().isParameterForRawStringReplace()) {
                    String value =  node.asLiteral().getLexicalForm();
                    query = new ParameterizedSparqlString(
                            query.toString().replaceAll(
                                    Pattern.quote("$"+ entry.getKey().getParameterName()), Matcher.quoteReplacement(value)));
                } else {
                    query.setParam(entry.getKey().getPredicate().getLocalName(), entry.getValue());
                }
            }
            // FIXME add fixed bindings e.g. $shape etc

            return query.toString().trim().replaceFirst("ASK", ""); // remove ASK...
        } catch (ARQException e) {
            // skip this exception
        }
        return bindedQuery;
    }

    public void validateSparqlQuery() {
        String originalSparqlQueryLowerCase = sparqlQuery.toUpperCase();
        if (
                originalSparqlQueryLowerCase.contains("VALUES") ||
                        originalSparqlQueryLowerCase.contains("SERVICE") ||
                        originalSparqlQueryLowerCase.contains("MINUS")) {
            throw new IllegalArgumentException("Pre-binding failed in query because of illegal constructs (VALUES, SERVICE, MINUS):\n" + sparqlQuery);
        }

        ImmutableSet<String> preboundVars = ImmutableSet.of("this", "shapesGraph", "currentShape", "value");
        preboundVars.forEach(var -> {
            if (sparqlQuery.matches("(?s).*[Aa][Ss]\\s+[\\?\\$]" + var + "\\W.*")) {
                throw new IllegalArgumentException("Pre-binding failed in query because of use of pre-bind variable with AS:\n" + sparqlQuery);
            }
        });
    }
}
