package org.aksw.rdfunit.model.impl.shacl;

import lombok.*;
import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;

import java.util.Map;
import java.util.Optional;

@ToString
@EqualsAndHashCode(exclude={"element"})
@Builder
public class ComponentValidatorImpl implements ComponentValidator {
    @Getter @NonNull private final Resource element;
    @Getter private final String message;
    @Getter @NonNull private final String sparqlQuery;
    @Getter @NonNull private final ComponentValidatorType type;
    @Getter private String filter;

    @Override
    public Optional<String> getDefaultMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    public boolean filterAppliesForBindings(Map<ComponentParameter, RDFNode> bindings) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        return canBind(filter, bindings);
    }


    private boolean canBind(String filter, Map<ComponentParameter, RDFNode> bindings) {
        String filterQ = filter;
        bindings.entrySet().forEach( e -> {
            filterQ.replace("$" + e.getKey().getParameterName(), formatRdfValue(e.getValue()));
        });
        return checkFilter(filterQ);
    }

    private boolean checkFilter(String askQuery) {
        Model m = ModelFactory.createDefaultModel();
        Query q = QueryFactory.create(askQuery);
        try (QueryExecution qex = org.apache.jena.query.QueryExecutionFactory.create(q, m)) {
            return qex.execAsk();
        }
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
