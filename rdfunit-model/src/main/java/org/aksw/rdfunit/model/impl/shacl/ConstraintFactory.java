package org.aksw.rdfunit.model.impl.shacl;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class ConstraintFactory {

    private ConstraintFactory() {}

    public static Optional<ComponentConstraint> createFromShapeAndComponent(Shape shape, Component component) {
        if (canBindComponentToShape(component, shape)) {
            ComponentConstraintImpl.ComponentConstraintImplBuilder constraintBuilder = ComponentConstraintImpl.builder();
            constraintBuilder
                    .shape(shape)
                    .component(component);


            Set<Property> parameterProperties = component.getParameters().stream().map(ComponentParameter::getPredicate).collect(Collectors.toSet());
            Set<PropertyValuePair> bindingPairs = shape.getPropertyValuePairSets().getAnnotations().stream()
                    .filter(p -> parameterProperties.contains(p.getProperty()))
                    .collect(Collectors.toSet());

            // TODO take multiple values into account
            Map<ComponentParameter, RDFNode> bindings = new HashMap<>();
            component.getParameters().forEach(parameter -> {
                Optional<RDFNode> node = bindingPairs.stream()
                        .filter(p -> p.getProperty().equals(parameter.getPredicate()))
                        .flatMap(p -> p.getValues().stream())
                        .findFirst();
                if (node.isPresent()) {
                    bindings.put(parameter, node.get());
                }
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


                return Optional.of(constraintBuilder.build());

            } else {
                if(!component.isPartial())
                log.warn("No validators found for shape {} and component {}", shape, component);
            }
        }

        return Optional.empty();
    }

    private static boolean canBindComponentToShape(Component component, Shape shape) {

        Set<Property> parameterPropertiesRequired = component.getParameters().stream().filter(ComponentParameter::isRequired).map(ComponentParameter::getPredicate).collect(Collectors.toSet());
        Set<Property> availableProperties = shape.getPropertyValuePairSets().getAnnotations().stream().map(PropertyValuePair::getProperty).collect(Collectors.toSet());

        return availableProperties.containsAll(parameterPropertiesRequired);
    }
}
