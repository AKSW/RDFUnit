package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.helper.PropertySingleValuePair;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public final class ConstraintFactory {

    private ConstraintFactory() {}

    public static Set<ComponentConstraint> createFromShapeAndComponent(Shape shape, Component component) {

        ImmutableSet.Builder<ComponentConstraint> constraints = ImmutableSet.builder();

        getComponentShapeBindings(component, shape).forEach( bindingPairs -> {
            ComponentConstraintImpl.ComponentConstraintImplBuilder constraintBuilder = ComponentConstraintImpl.builder();
            constraintBuilder
                    .shape(shape)
                    .component(component);

            Map<ComponentParameter, RDFNode> bindings = new HashMap<>();
            component.getParameters().forEach(parameter -> {
                Optional<RDFNode> node = bindingPairs.stream()
                        .filter(p -> p.getProperty().equals(parameter.getPredicate()))
                        .map(p -> p.getValue())
                        .findFirst();
                node.ifPresent(rdfNode -> bindings.put(parameter, rdfNode));
            });
            constraintBuilder.bindings(bindings);

            //validator picking
            Optional<ComponentValidator> validator =
                    component.getValidators().stream()
                            .filter(v -> v.filterAppliesForBindings(shape.getShapeType(), bindings))
                            .findFirst();
            if (validator.isPresent()) {

                // FIXME get message from Shape for override
                Literal errorMessage = shape.getMessage().orElse(ResourceFactory.createStringLiteral("Unknown Error"));
                if (validator.get().getDefaultMessage().isPresent()) {
                    errorMessage = validator.get().getDefaultMessage().get();
                }
                constraintBuilder
                        .message(errorMessage)
                        .validator(validator.get())
                ;

                constraints.add(constraintBuilder.build());
            } else {
                if(!component.isPartial())
                    log.warn("No validators found for shape {} and component {}", shape, component);
            }

        });

        return constraints.build();
    }

    private static boolean canBindComponentToShape(Component component, Shape shape) {

        Set<Property> parameterPropertiesRequired = component.getParameters().stream().filter(ComponentParameter::isRequired).map(ComponentParameter::getPredicate).collect(Collectors.toSet());
        Set<Property> availableProperties = shape.getPropertyValuePairSets().getAnnotations().stream().map(PropertyValuePair::getProperty).collect(Collectors.toSet());

        return availableProperties.containsAll(parameterPropertiesRequired);
    }

    private static Set<Set<PropertySingleValuePair>> getComponentShapeBindings(Component component, Shape shape) {

        Set<Property> parameterProperties = component.getParameters().stream().map(ComponentParameter::getPredicate).collect(Collectors.toSet());
        Set<PropertySingleValuePair> bindingPairs = shape.getPropertyValuePairSets().getAnnotations().stream()
                .filter(p -> parameterProperties.contains(p.getProperty()))
                .flatMap(p -> PropertySingleValuePair.create(p).stream())
                .collect(Collectors.toSet());

        if (bindingPairs.isEmpty()) {
            return ImmutableSet.of();
        }

        int maxParametersSize = parameterProperties.size();
        Set<Property> requiredParameters = component.getParameters().stream().filter(p -> !p.isOptional()).map(ComponentParameter::getPredicate).collect(Collectors.toSet());
        int requiredParameterSize = requiredParameters.size();

        ImmutableSet.Builder<Set<PropertySingleValuePair>> pairs = ImmutableSet.builder();
        for (int i = requiredParameterSize; i<=maxParametersSize && i <= bindingPairs.size(); i++ ) {
            pairs.addAll(Sets.combinations(bindingPairs, i));
        }

        return pairs.build().stream()
                .filter( pairA -> {
                    // delete pairs without the required parameters
                    List<Property> pairAProperties = pairA.stream().map(PropertySingleValuePair::getProperty).collect(Collectors.toList());

                    // check if it has duplicates (if set size is smaller that list size)
                    if (ImmutableSet.copyOf(pairAProperties).size() != pairAProperties.size()) {
                        return false;
                    }
                    // check if permutation has  required parameters
                    Set<Property> properties = new HashSet<>();
                    properties.addAll(pairAProperties);
                    properties.addAll(requiredParameters);
                    return properties.size() == pairAProperties.size();
                })
                .collect(Collectors.toSet());
    }
}
