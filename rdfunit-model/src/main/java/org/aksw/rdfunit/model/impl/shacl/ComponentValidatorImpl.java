package org.aksw.rdfunit.model.impl.shacl;

import lombok.*;
import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.enums.ShapeType;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;

import java.util.Arrays;
import java.util.List;
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
    @Getter private final String filter;

    private static final List<ComponentValidatorType> propertyValidators = Arrays.asList(ComponentValidatorType.ASK_VALIDATOR, ComponentValidatorType.PROPERTY_VALIDATOR);
    private static final List<ComponentValidatorType> nodeValidators = Arrays.asList(ComponentValidatorType.ASK_VALIDATOR, ComponentValidatorType.NODE_VALIDATOR);

    @Override
    public Optional<String> getDefaultMessage() {
        return Optional.ofNullable(message);
    }

    @Override
    public boolean filterAppliesForBindings(ShapeType shapeType, Map<ComponentParameter, RDFNode> bindings) {
        if (shapeType.equals(ShapeType.PROPERTY_SHAPE)  && !propertyValidators.contains(type)) {
            return false;
        }
        if (shapeType.equals(ShapeType.NODE_SHAPE)  && !nodeValidators.contains(type)) {
            return false;
        }
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        return canBind(filter, bindings);
    }


    private boolean canBind(String filter, Map<ComponentParameter, RDFNode> bindings) {
        String filterQ = filter;
        for ( Map.Entry<ComponentParameter, RDFNode> e: bindings.entrySet()) {
            filterQ = filterQ.replace("$" + e.getKey().getParameterName(), formatRdfValue(e.getValue()));
        }
        boolean canBind = checkFilter(filterQ);
        return canBind;
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
