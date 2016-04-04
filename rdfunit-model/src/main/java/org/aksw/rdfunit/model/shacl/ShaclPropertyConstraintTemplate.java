package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 11:20 μμ
 */
@Builder
@ToString
@EqualsAndHashCode
public class ShaclPropertyConstraintTemplate {
    @NonNull @Getter private final String sparqlPropSnippet;
    @NonNull @Getter private final String sparqlInvPSnippet;
    @NonNull @Getter @Singular private final ImmutableSet<Argument> arguments;
    @NonNull @Getter @Singular private final ImmutableMap<Argument, String> argumentFilters;
    @NonNull @Getter private final String message;
    @Getter private final boolean includePropertyFilter;

    public boolean canBind(PropertyValuePairSet propertyValuePairSet) {

        return argumentFilters.entrySet().stream()
                .allMatch(entry -> canBind(propertyValuePairSet, entry.getKey(), entry.getValue()));

    }

    private boolean canBind(PropertyValuePairSet propertyValuePairSet, Argument arg, String sparqlFilter) {
        String askQuery = generateQuery(propertyValuePairSet, arg, sparqlFilter);
        return checkFilter(askQuery);
    }

    private boolean checkFilter(String askQuery) {
        Model m = ModelFactory.createDefaultModel();
        Query q = QueryFactory.create(askQuery);
        try (QueryExecution qex = org.apache.jena.query.QueryExecutionFactory.create(q, m)) {
            return qex.execAsk();
        }
    }

    private String generateQuery(PropertyValuePairSet propertyValuePairSet, Argument arg, String sparqlFilter) {
        String replaceStr = "$" + arg.getPredicate().getLocalName();
        RDFNode value = propertyValuePairSet.getPropertyValues(arg.getPredicate()).stream().findFirst().get();
        return sparqlFilter.replace(replaceStr, formatRdfValue(value));
    }

    private String formatRdfValue(RDFNode value) {
        if (value.isResource()) {
            return asFullTurtleUri(value.asResource());
        } else {
            return asSimpleLiteral(value.asLiteral());
        }
    }

    private String asSimpleLiteral(Literal value) {
        return value.getLexicalForm();
    }

    private String asFullTurtleUri(Resource value) {
        // some vocabularies use spaces in uris
        return "<" + value.getURI().trim().replace(" ", "") + ">";
    }

}
