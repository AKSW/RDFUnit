package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.impl.shacl.ConstraintTestCaseFactory;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.readers.shacl.BatchComponentReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeTargetReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

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
            allTargets.entrySet().forEach(entry -> {
                testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromComponentAndShape(component, entry.getKey(), entry.getValue()));
            });
        });

        return testCaseBuilder.build();
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

        if (shape.isNodeShape()) {
            // use the exact same target
            childShapes.forEach(cs -> mergeValues(implicitTargets, cs, targets));
        }

        // recursive
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

    private List<Shape> getChildShapes(Shape shape) {
        List<Shape> shapes = new ArrayList<>();
        shapes.addAll(getChildNodeShapes(shape));
        shapes.addAll(getChildPropertyShapes(shape));
        return shapes;
    }

    private List<Shape> getChildNodeShapes(Shape shape) {
        return getChildShapes(shape, SHACL.node);
    }

    private List<Shape> getChildPropertyShapes(Shape shape) {
        return getChildShapes(shape, SHACL.property);
    }

    private List<Shape> getChildShapes(Shape shape, Property property) {
        ImmutableSet.Builder<RDFNode> nodeBuilder = ImmutableSet.builder();
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(property));
        return nodeBuilder.build().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(resourceShapeMap::get)
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }
}
