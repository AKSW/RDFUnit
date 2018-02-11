package org.aksw.rdfunit.model.readers.shacl;

import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM

 */
public final class BatchShapeReader {

    private static final ImmutableSet<Resource> shapesAsInstancesOf = ImmutableSet.of(SHACL.Shape, SHACL.NodeShape, SHACL.PropertyShape);
    private static final ImmutableSet<Property> shapesAsObjectsOf = ImmutableSet.of(SHACL.node, SHACL.property, SHACL.and, SHACL.or);
    private static final ImmutableSet<Property> shapesAsSubjectsOf = ImmutableSet.of(SHACL.node, SHACL.property, SHACL.and, SHACL.or,
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

        shapeResourceBuilder.build().parallelStream()
                .forEach(resource -> shapes.add(ShapeReader.create().read(resource)));

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


    private void addShapesAsSubjectsOf(Model model, ImmutableSet.Builder<Resource> shapes) {
        shapesAsSubjectsOf.forEach(r -> model.listSubjectsWithProperty(r).toSet().stream()
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .forEach(shapes::add));
    }

}
