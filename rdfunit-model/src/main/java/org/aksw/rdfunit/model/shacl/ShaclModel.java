package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.impl.shacl.ConstraintImpl;
import org.aksw.rdfunit.model.impl.shacl.TestCaseWithTarget;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.aksw.rdfunit.model.readers.shacl.BatchComponentReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeTargetReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a SHACL Model
 *
 * @author Dimitris Kontokostas
 * @since 29/1/2016 9:28 πμ
 */

@Slf4j
public class ShaclModel {
    @NonNull private final ShapesGraph shapesGraph;
    @NonNull private final ImmutableSet<Shape> shapes;
    @NonNull private final ImmutableMap<Shape, Set<ShapeTarget>> explicitTargets;
    @NonNull private final ImmutableMap<Shape, Set<ShapeTarget>> implicitTargets;
    @NonNull private final ImmutableMap<Shape, Set<ShapeTarget>> allTargets;
    @NonNull private final ImmutableMap<Resource, Shape> resourceShapeMap;


    // TODO do not use Model for instantiation, change later
    public ShaclModel(Model shaclGraph) throws RdfReaderException {

        // read templates from Model, for now only use fixed core

        this.shapes = ImmutableSet.copyOf(BatchShapeReader.create().getShapesFromModel(shaclGraph));

        this.shapesGraph = ShapesGraph.builder()
                .shapes(this.shapes)
                .components(BatchComponentReader.create().getComponentsFromModel(shaclGraph))
                .components(BatchComponentReader.create().getComponentsFromModel(RdfReaderFactory.createResourceReader(Resources.SHACL_CORE_CCs).read()))
                .build();

        resourceShapeMap = ImmutableMap.copyOf(
                shapes.stream().collect(Collectors.toMap(Shape::getElement,
                Function.identity())));

        this.explicitTargets = ImmutableMap.copyOf(getExplicitShapeTargets(shapes));
        this.implicitTargets = ImmutableMap.copyOf(getImplicitShapeTargets(shapes, explicitTargets));

        ImmutableMap.Builder<Shape, Set<ShapeTarget>> targetBuilder = ImmutableMap.builder();
        shapes.forEach(s -> {
            Set<ShapeTarget> targets = new HashSet<>();
            targets.addAll(explicitTargets.getOrDefault(s, Collections.emptySet()));
            targets.addAll(implicitTargets.getOrDefault(s, Collections.emptySet()));
            if (!targets.isEmpty()) {
                targetBuilder.put(s, targets);
            }
        });
        allTargets = targetBuilder.build();

    }

    public Set<Shape> getShapes() { return shapes;}

    public Set<TestCase> generateTestCases() {
        ImmutableSet.Builder<TestCase> testCaseBuilder = ImmutableSet.builder();

        shapesGraph.getComponents().forEach(component -> {
            shapesGraph.getShapes().forEach(shape -> {
                //bind component to shape
                if (canBindComponentToShape(component, shape)) {
                    ConstraintImpl.ConstraintImplBuilder constraintBuilder = ConstraintImpl.builder();
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
                        String errorMessage = "Test Message";
                        if (validator.get().getDefaultMessage().isPresent()) {
                            errorMessage = validator.get().getDefaultMessage().get();
                        }
                        constraintBuilder
                            .message(errorMessage)
                            .validator(validator.get())
                            .severity(shape.getSeverity());


                        constraintBuilder.build();

                        if (allTargets.containsKey(shape)) {
                            allTargets.get(shape).forEach(target ->
                                    testCaseBuilder.add(
                                            TestCaseWithTarget.builder()
                                                    .target(target)
                                                    .filterSpqrql("")
                                                    .testCase(constraintBuilder.build().getTestCase())
                                                    .build())
                            );
                        }
                    } else {
                        log.warn("No validators found for shape {} and component {}", shape, component);
                    }
                }
            });
        });




        return testCaseBuilder.build();
    }

    private boolean canBindComponentToShape(Component component, Shape shape) {

        Set<Property> parameterPropertiesRequired = component.getParameters().stream().filter(ComponentParameter::isRequired).map(ComponentParameter::getPredicate).collect(Collectors.toSet());
        Set<Property> availableProperties = shape.getPropertyValuePairSets().getAnnotations().stream().map(PropertyValuePair::getProperty).collect(Collectors.toSet());

        return availableProperties.containsAll(parameterPropertiesRequired);
    }


/*
    private Optional<ComponentValidator> getBindingValidator(PropertyValuePairSet propertyValuePairSet, Component component) {
        component.getValidators().stream()
                .filter(v -> )
        String askQuery = generateQuery(propertyValuePairSet, arg, sparqlFilter);
        checkFilter(askQuery);
    }*/
/*
    private String generateQuery(PropertyValuePairSet propertyValuePairSet, ComponentParameter arg, String sparqlFilter) {
        String replaceStr = "$" + arg.getPredicate().getLocalName();
        RDFNode value = propertyValuePairSet.getPropertyValues(arg.getPredicate()).stream().findFirst().get();
        return sparqlFilter.replace(replaceStr, formatRdfValue(value));
    }*/

    private boolean checkFilter(ComponentValidator validator) {
        String askQuery = validator.getFilter();
        Model m = ModelFactory.createDefaultModel();
        Query q = QueryFactory.create(askQuery);
        try (QueryExecution qex = org.apache.jena.query.QueryExecutionFactory.create(q, m)) {
            return qex.execAsk();
        }

    }
/*
    public Set<TestCase> generateTestCasesOld() {
        ImmutableSet.Builder<TestCase> builder = ImmutableSet.builder();

        getShapes().forEach( shape -> // for every shape
                shape.getTargets().forEach(target ->  // for every target (skip if none)
                                shape.getPropertyConstraintGroups().forEach( ppg ->
                                    ppg.getPropertyConstraints().forEach( ppc -> {
                                        String filter = ppg.getPropertyFilter();
                                        if (!ppc.usesValidatorFunction()) {filter = "";}
                                        builder.add(
                                                TestCaseWithTarget.builder()
                                                        .target(target)
                                                        .filterSpqrql(filter)
                                                        .testCase(ppc.getTestCase(ppg.isInverse()))
                                                        .build());
                                    } ))));

        return builder.build();
    }*/

    public Map<Shape, Set<ShapeTarget>> getExplicitShapeTargets(Collection<Shape> shapes) {
        Map<Shape, Set<ShapeTarget>> targets = new HashMap<>();
        shapes.forEach( s -> {
            Set<ShapeTarget> trgs = BatchShapeTargetReader.create().read(s.getElement());
            if (!trgs.isEmpty()) {
                targets.put(s, trgs);
            }
        });
        return  targets;
    }

    public Map<Shape, Set<ShapeTarget>> getImplicitShapeTargets(ImmutableSet<Shape> shapes, ImmutableMap<Shape, Set<ShapeTarget>> explicitTargets) {
        Map<Shape, Set<ShapeTarget>> implicitTargets = new HashMap<>();

        explicitTargets.forEach( (shape, targets) -> {
            getImplicitTargetsForSingleShape(implicitTargets, shape, targets);

        });

        return  implicitTargets;
    }

    private void getImplicitTargetsForSingleShape(Map<Shape, Set<ShapeTarget>> implicitTargets, Shape shape, Set<ShapeTarget> targets) {
        List<Shape> childShapes = getChildShapes(shape);
        if (shape.getPath().isPresent()) {

        } else {
            // use the exact same target
            childShapes.forEach(cs -> mergeValues(implicitTargets, cs, targets));

        }
        // recursive ... FIXME
        childShapes.forEach( cs -> {
            getImplicitTargetsForSingleShape(implicitTargets, cs, implicitTargets.getOrDefault(cs, Collections.emptySet()));
        });
    }

    private void mergeValues(Map<Shape, Set<ShapeTarget>> implicitTargets, Shape shape, Set<ShapeTarget> targets) {
        if (implicitTargets.containsKey(shape)) {
            implicitTargets.get(shape).addAll(targets);
        } else {
            implicitTargets.put(shape, targets);
        }
    }

    List<Shape> getChildShapes(Shape shape) {
        ImmutableSet.Builder<RDFNode> nodeBuilder = ImmutableSet.builder();
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(SHACL.node));
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(SHACL.property));
        return nodeBuilder.build().stream()
                .map(n -> n.asResource())
                .map(resourceShapeMap::get)
                .collect(Collectors.toList());
    }
}
