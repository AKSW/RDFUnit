package org.aksw.rdfunit.model.readers.shacl;

import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.model.impl.shacl.ShapeList;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.atlas.lib.tuple.Tuple2;
import org.apache.jena.atlas.lib.tuple.TupleFactory;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM

 */
public final class BatchShapeReader {

    private static final ImmutableSet<Resource> shapesAsInstancesOf = ImmutableSet.of(SHACL.Shape, SHACL.NodeShape, SHACL.PropertyShape);
    private static final ImmutableSet<Property> shapesAsObjectsOf = ImmutableSet.of(SHACL.node, SHACL.property);
    private static final ImmutableSet<Property> shapeListsAsObjectOf = ImmutableSet.of(SHACL.and, SHACL.or, SHACL.xone, SHACL.not);
    private static final ImmutableSet<Property> shapesAsSubjectsOf = ImmutableSet.of(SHACL.node, SHACL.property, SHACL.and, SHACL.or, SHACL.xone, SHACL.not,
            SHACL.clazz, SHACL.datatype, SHACL.nodeKind,
            SHACL.minCount, SHACL.maxCount,
            SHACL.minInclusive, SHACL.minExclusive, SHACL.maxInclusive, SHACL.maxExclusive,
            SHACL.minLength, SHACL.maxLength, SHACL.pattern, SHACL.flags, SHACL.uniqueLang,
            SHACL.equalz, SHACL.notEquals, SHACL.lessThan, SHACL.lessThanOrEquals,
            SHACL.hasValue, SHACL.in,
            SHACL.targetClass, SHACL.targetNode, SHACL.targetObjectsOf, SHACL.targetSubjectsOf,
            SHACL.path
    );

    private BatchShapeReader(){}

    public static BatchShapeReader create() { return new BatchShapeReader();}

    public Set<Shape> getShapesFromModel(Model model) {
        ConcurrentLinkedQueue<Shape> shapes = new ConcurrentLinkedQueue<>();

        ImmutableSet.Builder<Resource> shapeResourceBuilder = ImmutableSet.builder();

        addShapesAsInstancesOf(model, shapeResourceBuilder);
        addShapesAsObjectsOf(model, shapeResourceBuilder);
        addShapesAsSubjectsOf(model, shapeResourceBuilder);

        ImmutableSet<Resource> shapeResources = shapeResourceBuilder.build();
        shapeResources.forEach(resource -> shapes.add(ShapeReader.create().read(resource)));
        addShapeListsAsObjectsOf(model).forEach(ls -> {
            shapes.add(ls);
            shapes.addAll(ls.getShapes());
        });

        return ImmutableSet.copyOf(shapes);
    }

    private void addShapesAsInstancesOf(Model model, ImmutableSet.Builder<Resource> shapes) {
        shapesAsInstancesOf.forEach(shapeType -> shapes.addAll(model.listSubjectsWithProperty(RDF.type, shapeType)));
    }

    private void addShapesAsObjectsOf(Model model, ImmutableSet.Builder<Resource> shapes) {
        shapesAsObjectsOf.forEach(r -> model.listObjectsOfProperty(r).toSet().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .forEach(shapes::add));
    }

    private Collection<ShapeList> addShapeListsAsObjectsOf(Model model) {
        ArrayList<ShapeList> lists = new ArrayList<>();
        shapeListsAsObjectOf.forEach(property -> {
            Query q = QueryFactory.create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                    "SELECT ?list ?innerShape " +
                    "WHERE{" +
                    "   ?x <" + property.getURI() + "> ?list ." +
                    "   ?list rdf:rest*/rdf:first ?innerShape ." +
                    "}");
            QueryExecution qe = QueryExecutionFactory.create(q, model);
            ResultSet rs = qe.execSelect();
            ArrayList<Tuple2<Resource>> listShapes = new ArrayList<>();
            while(rs.hasNext()){
                QuerySolution qs = rs.next();
                listShapes.add(TupleFactory.create2(qs.getResource("list"), qs.getResource("innerShape")));
            }
            Map<Resource, List<Tuple2<Resource>>> grouped = listShapes.stream().collect(Collectors.groupingBy((Tuple2<Resource> t) -> t.get(0), Collectors.toList()));

            grouped.forEach((list, entries) -> {
                Shape listShape = ShapeReader.create().read(list);
                ImmutableSet<Shape> elements = ImmutableSet.copyOf(entries.stream().map(r -> ShapeReader.create().read(r.get(1))).collect(Collectors.toList()));
                lists.add(ShapeList.builder()
                        .element(listShape.getElement())
                        .propertyValuePairSets(listShape.getPropertyValuePairSets())
                        .shapes(elements)
                        .shaclPath(listShape.getPath().isPresent() ? listShape.getPath().get() : null)
                        .build());
            });
        });
        return lists;
    }

    private void addShapesAsSubjectsOf(Model model, ImmutableSet.Builder<Resource> shapes) {
        shapesAsSubjectsOf.forEach(r -> model.listSubjectsWithProperty(r).toSet().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .forEach(shapes::add));
    }

}
