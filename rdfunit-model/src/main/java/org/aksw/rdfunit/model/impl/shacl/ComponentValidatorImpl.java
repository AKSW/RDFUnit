package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.enums.ShapeType;
import org.aksw.rdfunit.model.helper.QueryPrebinding;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

@ToString
@EqualsAndHashCode(exclude = {"element"})
@Builder
@Slf4j
public class ComponentValidatorImpl implements ComponentValidator {

  private static final List<ComponentValidatorType> propertyValidators = Arrays
      .asList(ComponentValidatorType.ASK_VALIDATOR, ComponentValidatorType.PROPERTY_VALIDATOR);
  private static final List<ComponentValidatorType> nodeValidators = Arrays
      .asList(ComponentValidatorType.ASK_VALIDATOR, ComponentValidatorType.NODE_VALIDATOR);
  @Getter
  @NonNull
  private final Resource element;
  @Getter
  private final Literal message;
  @Getter
  @NonNull
  private final String sparqlQuery;
  @Getter
  @NonNull
  private final ComponentValidatorType type;
  @Getter
  @NonNull
  @Singular
  private final ImmutableSet<PrefixDeclaration> prefixDeclarations;
  @Getter
  private final String filter;

  @Override
  public Optional<Literal> getDefaultMessage() {
    return Optional.ofNullable(message);
  }

  @Override
  public boolean filterAppliesForBindings(Shape shape, Map<ComponentParameter, RDFNode> bindings) {
    if (shape.getShapeType().equals(ShapeType.PROPERTY_SHAPE) && !propertyValidators
        .contains(type)) {
      return false;
    }
    if (shape.getShapeType().equals(ShapeType.NODE_SHAPE) && !nodeValidators.contains(type)) {
      return false;
    }
    if (filter == null || filter.isEmpty()) {
      return true;
    }

    return canBind(filter, bindings, shape);
  }


  private boolean canBind(String filter, Map<ComponentParameter, RDFNode> bindings, Shape shape) {
    QueryPrebinding queryPrebinding = new QueryPrebinding(filter, shape);
    String filterBinded = queryPrebinding.applyBindings(bindings);
    boolean canBind = checkFilter("ASK" + filterBinded);
    return canBind;
  }

  private boolean checkFilter(String askQuery) {
    Model m = ModelFactory.createDefaultModel();
    try (QueryExecution qex = org.apache.jena.query.QueryExecutionFactory
        .create(QueryFactory.create(askQuery), m)) {
      return qex.execAsk();
    } catch (Exception e) {
      log.error("Error evaluating filter query: {}", askQuery, e);
      return false;
    }
  }


}
