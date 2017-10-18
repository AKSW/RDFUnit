package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.model.readers.shacl.ShapePathReader;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.aksw.rdfunit.model.helper.NodeFormatter.formatNode;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeTargetValueShape implements ShapeTarget {

    @Getter private final ShapeTargetType targetType = ShapeTargetType.ValueShapeTarget;
    private final ImmutableList<ShapePath> pathChain;
    private final ShapeTarget innerTarget;
    private final Function<ShapeTargetValueShape, String> generatePattern;

    private ShapeTargetValueShape(ShapeTarget innerTarget, List<ShapePath> pathChain, Function<ShapeTargetValueShape, String> generatePattern) {
        this.innerTarget = innerTarget;
        this.pathChain = ImmutableList.copyOf(pathChain.stream().filter(p -> p != null).collect(Collectors.toList()));
        this.generatePattern = generatePattern;
    }


    public static ShapeTarget create(ShapeTarget outerTarget, String property) {
        return create(outerTarget, ShapePathReader.create().read(ResourceFactory.createProperty(property)));
    }

    public static ShapeTarget create(ShapeTarget outerTarget, ShapePath path) {
        return create(outerTarget, Arrays.asList(path));
    }

    public static ShapeTarget create(ShapeTarget innerTarget, List<ShapePath> pathChain) {
        switch (innerTarget.getTargetType()) {
            case ClassTarget:
                return new ShapeTargetValueShape(innerTarget, pathChain, ShapeTargetValueShape::classTargetPattern);
            case NodeTarget:
                return new ShapeTargetValueShape(innerTarget, pathChain, ShapeTargetValueShape::nodeTargetPattern);
            case ObjectsOfTarget:
                return new ShapeTargetValueShape(innerTarget, pathChain, ShapeTargetValueShape::objectsOfTargetPattern);
            case SubjectsOfTarget:
                return new ShapeTargetValueShape(innerTarget, pathChain, ShapeTargetValueShape::subjectsOfTargetPattern);
            case ValueShapeTarget:
                if (innerTarget instanceof ShapeTargetValueShape) {
                    return transformTarget( (ShapeTargetValueShape) innerTarget, pathChain);
                }  else {
                    throw new IllegalArgumentException("Unsupported target in sh:node");
                }
            default:
                throw new IllegalArgumentException("Unsupported target in sh:node");
        }
    }

    @Override
    public RDFNode getNode() {
        return innerTarget.getNode();
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(this);
    }

    @Override
    public Set<Resource> getRelatedOntologyResources() {
        ImmutableSet.Builder<Resource> builder = ImmutableSet.builder();

        pathChain.forEach(p -> builder.addAll(p.getUsedProperties()));
        builder.addAll(innerTarget.getRelatedOntologyResources());
        return builder.build();
    }


    private static ShapeTarget transformTarget(ShapeTargetValueShape innerTarget, List<ShapePath> pathChain) {
        ImmutableList.Builder<ShapePath> builder = new ImmutableList.Builder<>();
        builder.addAll(innerTarget.pathChain);
        builder.addAll(pathChain);
        return create(innerTarget.innerTarget, builder.build());
    }

    private static String classTargetPattern(ShapeTargetValueShape target) {
        return " ?focusNode rdf:type/rdfs:subClassOf* " + formatNode(target.getNode()) + " ; " +
                writePropertyChain(target.pathChain) + "  ?this . ";
    }

    // FIXME add focus node
    private static String nodeTargetPattern(ShapeTargetValueShape target) {
        return " " + formatNode(target.getNode()) + " " + writePropertyChain(target.pathChain) + " ?this . ";
    }

    private static String objectsOfTargetPattern(ShapeTargetValueShape target) {
        return " ?focusNode (^" + formatNode(target.getNode()) + ")/" + writePropertyChain(target.pathChain) + " ?this .";
    }

    private static String subjectsOfTargetPattern(ShapeTargetValueShape target) {
        return " ?focusNode " + formatNode(target.getNode()) + "/" + writePropertyChain(target.pathChain) + " ?this .";
    }

    private static String writePropertyChain(List<ShapePath> propertyChain) {
        return propertyChain.stream()
                .map(ShapePath::asSparqlPropertyPath)
                .collect(Collectors.joining(" / "));
    }
}
