package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.shacl.*;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.interfaces.shacl.TargetBasedTestCase;
import org.aksw.rdfunit.model.readers.shacl.BatchComponentReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeReader;
import org.aksw.rdfunit.model.readers.shacl.BatchShapeTargetReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.shared.uuid.JenaUUID;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a SHACL Model
 *
 * @author Dimitris Kontokostas
 * @since 29/1/2016 9:28 πμ
 */

@Slf4j
public class ShaclModel {
    @NonNull private final ShapesGraph shapesGraph;
    @NonNull private final ImmutableList<ShapeGroup> allShapeGroup;
    @NonNull private final ImmutableMap<Resource, Shape> resourceShapeMap;

    // providing parent shapes for root shapes, will not be passed to TestGenerator
    private static final Shape defaultParentShape = ShapeImpl.builder()
            .element(ResourceFactory.createResource(JenaUUID.generate().asString()))
            .propertyValuePairSets(PropertyValuePairSet.builder().build())
            .build();

    // TODO do not use Model for instantiation, change later
    public ShaclModel(Model inputShaclGraph) throws RdfReaderException {

        Model shaclGraph = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
        //shaclGraph.read(ShaclModel.class.getResourceAsStream("/org/aksw/rdfunit/configuration/shacl.ttl"), null, RDFLanguages.TURTLE.getName());
        shaclGraph.add(inputShaclGraph);
        // read templates from Model, for now only use fixed core

        final ImmutableList<Shape> shapes = ImmutableList.copyOf(BatchShapeReader.create().getShapesFromModel(shaclGraph));

        this.shapesGraph = ShapesGraph.builder()
                .shapes(shapes)
                .components(BatchComponentReader.create().getComponentsFromModel(shaclGraph))
                .components(BatchComponentReader.create().getComponentsFromModel(RdfReaderFactory.createResourceReader(Resources.SHACL_CORE_CCs).read()))
                .build();

        resourceShapeMap = ImmutableMap.copyOf(
                shapes.stream()
                        .distinct()
                        .collect(Collectors.toMap(Shape::getElement, Function.identity())));

        ImmutableList<Map.Entry<Shape, Set<ShapeTarget>>> explicitTargets = ImmutableList.copyOf(getExplicitShapeTargets(shapes));
        ImmutableList<ShapeGroup> implicitTargets = ImmutableList.copyOf(getImplicitShapeTargets(explicitTargets));
        ImmutableList.Builder<ShapeGroup> ats = new ImmutableList.Builder<>();
        ats.addAll(implicitTargets);
        ats.add(new ShapeGroup(explicitTargets, SHACL.LogicalConstraint.atomic, ImmutableList.of(), defaultParentShape));
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
    private Map<ShapeTarget, List<TargetBasedTestCase>> getGenericTestCase(ShapeGroup shapeGroup){
        ImmutableMap.Builder<ShapeTarget, List<TargetBasedTestCase>> testCaseBuilder = ImmutableMap.builder();
        extractGenericTest(shapeGroup).forEach((target, testCases) ->{
            ImmutableList.Builder<TargetBasedTestCase> list = ImmutableList.builder();
            if(! testCases.isEmpty()) {
                switch (shapeGroup.getGroupOperator()) {
                    case or:
                        list.add(new TestCaseGroupOr(ImmutableList.copyOf(testCases), shapeGroup.parentShape));
                        break;
                    case not:
                        list.add(new TestCaseGroupNot(ImmutableList.copyOf(testCases), shapeGroup.parentShape));
                        break;
                    case xone:
                        list.add(new TestCaseGroupXone(ImmutableList.copyOf(testCases), shapeGroup.parentShape));
                        break;
                    case and:
                        list.add(new TestCaseGroupAnd(ImmutableList.copyOf(testCases), shapeGroup.parentShape));
                        break;
                    case node:
                        list.add(new TestCaseGroupNode(ImmutableList.copyOf(testCases), shapeGroup.parentShape));
                        break;
                    default:
                        if(shapeGroup.getParentShape().isNodeShape() && shapeGroup.getSubGroups().stream().noneMatch(x -> x.getParentShape().isNodeShape())){
                            list.add(new TestCaseGroupNode(ImmutableList.copyOf(testCases), shapeGroup.parentShape));    // if parent is a sh:NodeShape we group all child shapes in an and relationship
                        }
                        else {  // else we treat all of them as atomics
                            list.addAll(testCases.stream().map(tc -> new TestCaseGroupAtomic(ImmutableList.of(tc), shapeGroup.parentShape)).collect(Collectors.toSet()));
                        }
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
    private Map<ShapeTarget, List<? extends TargetBasedTestCase>> extractGenericTest(ShapeGroup shapeGroup) {
        ImmutableList.Builder<TestCaseWithTarget> testCaseBuilder = ImmutableList.builder();
            shapeGroup.getAllTargets().forEach(shapeTargets -> {
                // SPARQL constraints
                testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromSparqlContraintInShape(shapeTargets.getKey(), shapeTargets.getValue()));
                // Constraint components
                shapesGraph.getComponents().forEach(component ->
                        testCaseBuilder.addAll(ConstraintTestCaseFactory.createFromComponentAndShape(component, shapeTargets.getKey(), shapeTargets.getValue())));
            });
        HashMap<ShapeTarget, List<? extends TargetBasedTestCase>> ret = new HashMap<>(
                testCaseBuilder.build().stream().collect(Collectors.groupingBy(TestCaseWithTarget::getTarget)));
        shapeGroup.getSubGroups().forEach(sub ->{
            getGenericTestCase(sub).forEach((target, tests) ->{
                List<? extends TargetBasedTestCase> nts = ret.get(target);
                if(nts == null)
                    nts = new ArrayList<>(tests);
                else{
                    List<TargetBasedTestCase> zw = new ArrayList<>(tests);
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
    private List<Map.Entry<Shape, Set<ShapeTarget>>> getExplicitShapeTargets(Collection<Shape> shapes) {
        List<Map.Entry<Shape, Set<ShapeTarget>>> targets = new ArrayList<>();
        shapes.forEach( s -> {
            Set<ShapeTarget> trgs = BatchShapeTargetReader.create().read(s.getElement());
            if (!trgs.isEmpty()) {
                targets.add(new AbstractMap.SimpleEntry(s, trgs));
            }
        });
        return  targets;
    }

    /**
     * Collects all implicit targets as a set of ShapeGroup for the given list of shapes
     */
    private List<ShapeGroup> getImplicitShapeTargets(ImmutableList<Map.Entry<Shape, Set<ShapeTarget>>> explicitTargets) {
        List<ShapeGroup> groups = new ArrayList<>();
        explicitTargets.forEach( shapeTargets -> groups.addAll(getImplicitTargetsForSingleShape(shapeTargets.getKey(), shapeTargets.getValue(), false)));
        return groups;
    }

    /**
     * Collects all implicit targets as a set of ShapeGroup
     */
    private List<ShapeGroup> getImplicitTargetsForSingleShape(final Shape shape, Set<ShapeTarget> targets, final boolean insideLogicalConstraint) {
        if (shape.isPropertyShape()) {
            assert(shape.getPath().isPresent());
            // use the exact same target
            targets = targets.stream()
                    .map(target -> ShapeTargetValueShape.create(target, shape.getPath().get()))
                    .collect(Collectors.toSet());
        }

        //collect shape groups
        List<ShapeGroup> targetGroups = new ArrayList<>();
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.and, insideLogicalConstraint));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.or, insideLogicalConstraint));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.not, insideLogicalConstraint));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.xone, insideLogicalConstraint));
        targetGroups.addAll(getShapeGroup(shape, targets, SHACL.LogicalConstraint.atomic, insideLogicalConstraint));

        return targetGroups;
    }

    /**
     * Will collect child shapes which are grouped by a given logical constraint, and create ShapeGroups reflecting this semantic connection
     * @param shape - the parent shape
     * @param targets - the known targets
     * @param groupOperator - the logical operator to collect child nodes for
     * @return - a set of ShapeGroup having the same logical operator
     */
    private List<ShapeGroup> getShapeGroup(
            final Shape shape,
            final Set<ShapeTarget> targets,
            final SHACL.LogicalConstraint groupOperator,
            final boolean insideLogicalConstraint
    ){
        ImmutableList.Builder<List<Shape>> childShapeSetsBuilder = ImmutableList.builder();
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
                childShapeSetsBuilder.add(ImmutableList.copyOf(getChildShapes(shape)));
        }
        List<List<Shape>> childShapeSets = childShapeSetsBuilder.build();
        if(childShapeSets.isEmpty() || childShapeSets.stream().allMatch(List::isEmpty))
            return ImmutableList.of();

        return childShapeSets.stream().map(css ->{
            List<Map.Entry<Shape, Set<ShapeTarget>>> targetMap = new ArrayList<>();
            ImmutableList.Builder<ShapeGroup> subs = new ImmutableList.Builder<>();
            css.stream().filter(s -> ! s.isDeactivated()).forEach(cs -> {           //recursive retrieval of ShapeGroups
                Shape enrichedShape = enrichPropertyShape(shape, cs);               //enrich with certain annotations of parent
                targetMap.add(new AbstractMap.SimpleEntry(enrichedShape, targets));
                subs.addAll(getImplicitTargetsForSingleShape(enrichedShape, targets, groupOperator != SHACL.LogicalConstraint.atomic));
            });
            // shape sets with more than one shape and no logical constraint default back to sh:and
            SHACL.LogicalConstraint operator = groupOperator;
            if(operator == SHACL.LogicalConstraint.atomic && css.size() > 1){
                if(shape.isNodeShape() && insideLogicalConstraint)
                    operator = SHACL.LogicalConstraint.and;
                else
                    operator = SHACL.LogicalConstraint.node;
            }
            return new ShapeGroup(targetMap, operator, subs.build(), shape);          // create new ShapeGroup
        }).collect(Collectors.toList());
    }

    /**
     * Will enrich the sub-shape of a property shape with its parents path annotation (so that the result annotations contain a resultPath for each of its sub results)
     * @param parent - the parent shape
     * @param child - the child shape
     * @return - enriched shape
     */
    private Shape enrichPropertyShape(Shape parent, Shape child){
    if(parent.isPropertyShape()) {
            return ShapeImpl.builder()
                    .element(child.getElement())
                    .shaclPath(child.getPath().orElse(null))
                    .propertyValuePairSets(child.getPropertyValuePairSets().getAnnotations().stream().anyMatch(x -> x.getProperty().equals(SHACL.path)) ?   //if path annotation is already present
                            child.getPropertyValuePairSets() :
                            new PropertyValuePairSet(ImmutableSet.copyOf(   //else add path annotation of parent
                                    Stream.concat(child.getPropertyValuePairSets().getAnnotations().stream(),
                                            parent.getPropertyValuePairSets().getAnnotations().stream().filter(a -> a.getProperty().equals(SHACL.path)))
                                            .collect(Collectors.toSet())))
                    )
                    .build();
        }
        else{
            return child;
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

    /**
     * Will collect all child shapes connected via the given property to the parent shape.
     * @param shape - the parent shape
     * @param property - the property to look for
     * @return - set of relevant child shapes
     */
    private List<Shape> getChildShapes(Shape shape, Property property) {
        ImmutableSet.Builder<RDFNode> nodeBuilder = ImmutableSet.builder();
        nodeBuilder.addAll(shape.getPropertyValuePairSets().getPropertyValues(property));
        return nodeBuilder.build().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(resourceShapeMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Shape> getChildNotShapes(Shape shape) {
        return getChildShapes(shape, SHACL.not);
    }

    private List<List<Shape>> getChildAndShapes(Shape shape) {
        return getChildListShapes(shape, SHACL.and);
    }

    private List<List<Shape>> getChildOrShapes(Shape shape) {
        return getChildListShapes(shape, SHACL.or);
    }

    private List<List<Shape>> getChildXorShapes(Shape shape) {
        return getChildListShapes(shape, SHACL.xone);
    }

    /**
     * Will collect all child shapes connected via the given property to the parent shape and expected to be collected in a rdf:List.
     * @param shape - the parent shape
     * @param property - the property to look for
     * @return - set of relevant child shapes grouped by the list they were in
     */
    private List<List<Shape>> getChildListShapes(Shape shape, Property property) {
        ImmutableList.Builder<RDFNode> nodeBuilder = ImmutableList.builder();
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
        @NonNull @Getter private final List<Map.Entry<Shape, Set<ShapeTarget>>> allTargets;
        @NonNull @Getter private final SHACL.LogicalConstraint groupOperator;
        @NonNull @Getter private final List<ShapeGroup> subGroups;
        @NonNull @Getter private final Shape parentShape;

        private ShapeGroup(List<Map.Entry<Shape, Set<ShapeTarget>>> allTargets, SHACL.LogicalConstraint groupOperator, List<ShapeGroup> subGroups, Shape parentShape) {
            this.allTargets = allTargets;
            this.groupOperator = groupOperator;
            this.subGroups = subGroups;
            this.parentShape = parentShape;
        }
    }
}
