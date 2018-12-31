package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableList;
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

    /**
     * Fetches all tests as a hierarchical collection
     */
    public Set<GenericTestCase> generateTestCases() {
        return allShapeGroup.stream().flatMap(groupShape -> getGenericTestCase(groupShape).values().stream().flatMap(Collection::stream)).collect(Collectors.toSet());
    }

    /**
     * Will generate a set of test trees from a given ShapeGroup, based on the logical operator of the group
     * @param shapeGroup - the ShapeGroup
     */
    private Map<ShapeTarget, List<GenericTestCase>> getGenericTestCase(ShapeGroup shapeGroup){
        ImmutableMap.Builder<ShapeTarget, List<GenericTestCase>> testCaseBuilder = ImmutableMap.builder();
        extractGenericTest(shapeGroup).forEach((target, testCases) ->{
            ImmutableList.Builder<GenericTestCase> list = ImmutableList.builder();
            if(! testCases.isEmpty()) {
                switch (shapeGroup.getGroupOperator()) {
                    case or:
                        list.add(new TestCaseGroupOr(ImmutableSet.copyOf(testCases)));
                        break;
                    case not:
                        list.add(new TestCaseGroupNot(ImmutableSet.copyOf(testCases)));
                        break;
                    case xone:
                        list.add(new TestCaseGroupXone(ImmutableSet.copyOf(testCases)));
                        break;
                    case and:
                        list.add(new TestCaseGroupAnd(ImmutableSet.copyOf(testCases)));
                        break;
                    default:        // and & atomic
                        list.addAll(testCases.stream().map(tc -> new TestCaseGroupAtomic(ImmutableSet.of(tc))).collect(Collectors.toSet()));
                        break;
                }
            }
            testCaseBuilder.put(target, list.build());
        });

        return testCaseBuilder.build();
    }

    /**
     * Will generate a set of test trees from a given ShapeGroup
     * @param shapeGroup - the ShapeGroup
     */
    private Map<ShapeTarget, List<? extends GenericTestCase>> extractGenericTest(ShapeGroup shapeGroup) {
        ImmutableSet.Builder<TestCaseWithTarget> testCaseBuilder = ImmutableSet.builder();
            shapeGroup.getAllTargets().forEach((shape, targets) -> {
                // SPARQL constraints
                testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromSparqlContraintInShape(shape, targets));
                // Constraint components
                shapesGraph.getComponents().forEach(component ->
                        testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromComponentAndShape(component, shape, targets)));
            });
        HashMap<ShapeTarget, List<? extends GenericTestCase>> ret = new HashMap<>(
                testCaseBuilder.build().stream().collect(Collectors.groupingBy(TestCaseWithTarget::getTarget)));
        shapeGroup.getSubGroups().forEach(sub ->{
            getGenericTestCase(sub).forEach((target, tests) ->{
                List<? extends GenericTestCase> nts = ret.get(target);
                if(nts == null)
                    nts = new ArrayList<>(tests);
                else{
                    List<GenericTestCase> zw = new ArrayList<>(tests);
                    zw.addAll(nts);
                    nts = zw;
                }
                ret.put(target, nts);
            });
        });
        return ret;
    }

    /**
     * Collects all explicit targets for the given shapes
     */
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

    /**
     * Collects all implicit targets as a set of ShapeGroup for the given list of shapes
     */
    private Set<ShapeGroup> getImplicitShapeTargets(ImmutableMap<Shape, Set<ShapeTarget>> explicitTargets) {
        Set<ShapeGroup> groups = new HashSet<>();
        explicitTargets.forEach( (shape, targets) -> groups.addAll(getImplicitTargetsForSingleShape(shape, targets)));
        return groups;
    }

    /**
     * Collects all implicit targets as a set of ShapeGroup
     */
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

    /**
     * Will collect child shapes which are grouped by a given logical constraint, and create ShapeGroups reflecting this semantic connection
     * @param shape - the parent shape
     * @param targets - the known targets
     * @param groupOperator - the logical operator to collect child nodes for
     * @return - a set of ShapeGroup having the same logical operator
     */
    private Set<ShapeGroup> getShapeGroup(final Shape shape, final Set<ShapeTarget> targets, final SHACL.LogicalConstraint groupOperator){
        ImmutableList.Builder<Set<Shape>> childShapeSetsBuilder = ImmutableList.builder();
        switch(groupOperator){
            case and:
                childShapeSetsBuilder.addAll(getChildAndShapes(shape));
                break;
            case or:
                childShapeSetsBuilder.addAll(getChildOrShapes(shape));
                break;
            case not:
                childShapeSetsBuilder.add(getChildNotShapes(shape));
                break;
            case xone:
                childShapeSetsBuilder.addAll(getChildXorShapes(shape));
                break;
            default:
                childShapeSetsBuilder.add(ImmutableSet.copyOf(getChildShapes(shape)));
        }
        List<Set<Shape>> childShapeSets = childShapeSetsBuilder.build();
        if(childShapeSets.isEmpty() || childShapeSets.stream().allMatch(Set::isEmpty))
            return ImmutableSet.of();

        return childShapeSets.stream().map(css ->{
            Map<Shape, Set<ShapeTarget>> targetMap = new HashMap<>();
            ImmutableSet.Builder<ShapeGroup> subs = new ImmutableSet.Builder<>();
            css.stream().filter(s -> ! s.isDeactivated()).forEach(cs -> {           //recursive retrieval of ShapeGroups
                targetMap.put(cs, targets);
                subs.addAll(getImplicitTargetsForSingleShape(cs, targets));
            });
            SHACL.LogicalConstraint operator = groupOperator == SHACL.LogicalConstraint.atomic ? SHACL.LogicalConstraint.and : groupOperator;
            return new ShapeGroup(targetMap, operator, subs.build());
        }).collect(Collectors.toSet());
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

    /**
     * Will collect all child shapes connected via the given property to the parent shape.
     * @param shape - the parent shape
     * @param property - the property to look for
     * @return - set of relevant child shapes
     */
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

    /**
     * Will collect all child shapes connected via the given property to the parent shape and expected to be collected in a rdf:List.
     * @param shape - the parent shape
     * @param property - the property to look for
     * @return - set of relevant child shapes grouped by the list they were in
     */
    private List<Set<Shape>> getChildListShapes(Shape shape, Property property) {
        ImmutableSet.Builder<RDFNode> nodeBuilder = ImmutableSet.builder();
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(property));
        return nodeBuilder.build().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(r -> ((ShapeList)resourceShapeMap.get(r)).getShapes())
                .collect(Collectors.toList());
    }

    /**
     * Intermediate struct linking a Map of child nodes and their targets to an (optional) logical operator (for logical constraints) and a list of sub groups
     */
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
