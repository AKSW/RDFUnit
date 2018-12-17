package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.impl.shacl.*;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.readers.shacl.BatchComponentReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeTargetReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.ontology.OntModelSpec;
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
    @NonNull private final ImmutableSet<ShapeGroup> allShapeGroup;
    @NonNull private final ImmutableMap<Resource, Shape> resourceShapeMap;

    // TODO do not use Model for instantiation, change later
    public ShaclModel(Model inputShaclGraph) throws RdfReaderException {

        Model shaclGraph = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
        //shaclGraph.read(ShaclModel.class.getResourceAsStream("/org/aksw/rdfunit/configuration/shacl.ttl"), null, RDFLanguages.TURTLE.getName());
        shaclGraph.add(inputShaclGraph);
        // read templates from Model, for now only use fixed core

        final ImmutableSet<Shape> shapes = ImmutableSet.copyOf(BatchShapeReader.create().getShapesFromModel(shaclGraph));

        this.shapesGraph = ShapesGraph.builder()
                .shapes(shapes)
                .components(BatchComponentReader.create().getComponentsFromModel(shaclGraph))
                .components(BatchComponentReader.create().getComponentsFromModel(RdfReaderFactory.createResourceReader(Resources.SHACL_CORE_CCs).read()))
                .build();

        resourceShapeMap = ImmutableMap.copyOf(
                shapes.stream().collect(Collectors.toMap(Shape::getElement,
                Function.identity())));

        ImmutableMap<Shape, Set<ShapeTarget>> explicitTargets = ImmutableMap.copyOf(getExplicitShapeTargets(shapes));
        ImmutableSet<ShapeGroup> implicitTargets = ImmutableSet.copyOf(getImplicitShapeTargets(explicitTargets));
        ImmutableSet.Builder<ShapeGroup> ats = new ImmutableSet.Builder<>();
        ats.addAll(implicitTargets);
        ats.add(new ShapeGroup(explicitTargets, SHACL.LogicalConstraint.atomic, ImmutableSet.of()));
        allShapeGroup = ats.build();
    }

    public Set<GenericTestCase> generateTestCases() {
        return allShapeGroup.stream().flatMap(groupShape -> getGenericTestCase(groupShape).stream()).collect(Collectors.toSet());
    }

    private Set<GenericTestCase> getGenericTestCase(ShapeGroup groupShape){
        ImmutableSet.Builder<GenericTestCase> testCaseBuilder = ImmutableSet.builder();
        switch (groupShape.getGroupOperator()){
            case and:
                testCaseBuilder.add(new TestCaseGroupAnd(extractGenericTest(groupShape)));
                break;
            case or:
                testCaseBuilder.add(new TestCaseGroupOr(extractGenericTest(groupShape)));
                break;
            case not:
                testCaseBuilder.add(new TestCaseGroupNot(extractGenericTest(groupShape)));
                break;
            case xone:
                testCaseBuilder.add(new TestCaseGroupXone(extractGenericTest(groupShape)));
                break;
            case atomic:
                extractGenericTest(groupShape).forEach(tc -> testCaseBuilder.add(new TestCaseSingletonGroup(Collections.singleton(tc))));
                break;
        }
        return testCaseBuilder.build();
    }

    private Set<GenericTestCase> extractGenericTest(ShapeGroup shapeGroup) {
        ImmutableSet.Builder<GenericTestCase> testCaseBuilder = ImmutableSet.builder();
            shapeGroup.getAllTargets().forEach((shape, targets) -> {
                // SPARQL constraints
                testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromSparqlContraintInShape(shape, targets));
                // Constraint components
                shapesGraph.getComponents().forEach(component ->
                        testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromComponentAndShape(component, shape, targets)));
            });
        shapeGroup.getSubGroups().forEach(sub ->{
            Set<GenericTestCase> zw = getGenericTestCase(sub);
            testCaseBuilder.addAll(zw);
        });
        return testCaseBuilder.build();
    }

    private Map<Shape, Set<ShapeTarget>> getExplicitShapeTargets(Collection<Shape> shapes) {
        Map<Shape, Set<ShapeTarget>> targets = new HashMap<>();
        shapes.forEach( s -> {
            Set<ShapeTarget> trgs = BatchShapeTargetReader.create().read(s.getElement());
            if (!trgs.isEmpty()) {
                targets.put(s, trgs);
            }
        });
        return  targets;
    }

    private Set<ShapeGroup> getImplicitShapeTargets(ImmutableMap<Shape, Set<ShapeTarget>> explicitTargets) {
        Set<ShapeGroup> groups = new HashSet<>();
        explicitTargets.forEach( (shape, targets) -> groups.addAll(getImplicitTargetsForSingleShape(shape, targets)));
        return groups;
    }

    private Set<ShapeGroup> getImplicitTargetsForSingleShape(Shape shape, Set<ShapeTarget> targets) {
        if (shape.isPropertyShape()) {
            assert(shape.getPath().isPresent());
            // use the exact same target
            targets = targets.stream()
                    .map(target -> ShapeTargetValueShape.create(target, shape.getPath().get()))
                    .collect(Collectors.toSet());
        }

        //collect shape groups
        Set<ShapeGroup> targetGroups = new HashSet<>();
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.and));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.or));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.not));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.xone));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.atomic));

        return targetGroups;
    }

    private Set<ShapeGroup> getShapeGroup(final Shape shape, final Set<ShapeTarget> targets, final SHACL.LogicalConstraint groupOperator){
        List<Set<Shape>> childShapeSets = new ArrayList<>();
        switch(groupOperator){
            case and:
                childShapeSets.addAll(getChildAndShapes(shape));
                break;
            case or:
                childShapeSets.addAll(getChildOrShapes(shape));
                break;
            case not:
                childShapeSets.add(getChildNotShapes(shape));
                break;
            case xone:
                childShapeSets.addAll(getChildXorShapes(shape));
                break;
            default:
                childShapeSets.add(ImmutableSet.copyOf(getChildShapes(shape)));
        }
        if(childShapeSets.isEmpty() || childShapeSets.stream().allMatch(Set::isEmpty))
            return ImmutableSet.of();

        return childShapeSets.stream().map(css ->{
            Map<Shape, Set<ShapeTarget>> targetMap = new HashMap<>();
            ImmutableSet.Builder<ShapeGroup> subs = new ImmutableSet.Builder<>();
            css.stream().filter(s -> ! s.isDeactivated()).forEach(cs -> {           //recursive retrieval of ShapeGroups
                mergeValues(targetMap, cs, targets);
                subs.addAll(getImplicitTargetsForSingleShape(cs, targets));
            });
            return new ShapeGroup(targetMap, groupOperator, subs.build());
        }).collect(Collectors.toSet());
    }

    private void mergeValues(Map<Shape, Set<ShapeTarget>> implicitTargets, Shape shape, Set<ShapeTarget> targets) {
        if (implicitTargets.containsKey(shape)) {
            implicitTargets.get(shape).addAll(targets);
        } else {
            HashSet<ShapeTarget> targetsCopy = new HashSet<>(targets);
            implicitTargets.put(shape, targetsCopy);
        }
    }

    private List<Shape> getChildShapes(Shape shape) {
        List<Shape> shapes = new ArrayList<>();
        shapes.addAll(getChildNodeShapes(shape));
        shapes.addAll(getChildPropertyShapes(shape));
        return shapes;
    }

    private Set<Shape> getChildNodeShapes(Shape shape) {
        return getChildShapes(shape, SHACL.node);
    }

    private Set<Shape> getChildPropertyShapes(Shape shape) {
        return getChildShapes(shape, SHACL.property);
    }

    private Set<Shape> getChildShapes(Shape shape, Property property) {
        ImmutableSet.Builder<RDFNode> nodeBuilder = ImmutableSet.builder();
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(property));
        return nodeBuilder.build().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(resourceShapeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Shape> getChildNotShapes(Shape shape) {
        return getChildShapes(shape, SHACL.not);
    }

    private List<Set<Shape>> getChildAndShapes(Shape shape) {
        return getChildListShapes(shape, SHACL.and);
    }

    private List<Set<Shape>> getChildOrShapes(Shape shape) {
        return getChildListShapes(shape, SHACL.or);
    }

    private List<Set<Shape>> getChildXorShapes(Shape shape) {
        return getChildListShapes(shape, SHACL.xone);
    }


    private List<Set<Shape>> getChildListShapes(Shape shape, Property property) {
        ImmutableSet.Builder<RDFNode> nodeBuilder = ImmutableSet.builder();
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(property));
        return nodeBuilder.build().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(r -> ((ShapeList)resourceShapeMap.get(r)).getShapes())
                .collect(Collectors.toList());
    }

    private class ShapeGroup{
        @NonNull @Getter private final Map<Shape, Set<ShapeTarget>> allTargets;
        @NonNull @Getter private final SHACL.LogicalConstraint groupOperator;
        @NonNull @Getter private final Set<ShapeGroup> subGroups;

        private ShapeGroup(Map<Shape, Set<ShapeTarget>> allTargets, SHACL.LogicalConstraint groupOperator, Set<ShapeGroup> subGroups) {
            this.allTargets = allTargets;
            this.groupOperator = groupOperator;
            this.subGroups = subGroups;
        }
    }
}
