package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeTargetValueShape implements ShapeTarget {

    @Getter private final ShapeTargetType targetType = ShapeTargetType.ValueShapeTarget;
    private final ImmutableList<Resource> propertyChain;
    private final ShapeTarget innerTarget;
    private final Function<ShapeTargetValueShape, String> generatePattern;

    private ShapeTargetValueShape(ShapeTarget innerTarget, List<Resource> propertyChain, Function<ShapeTargetValueShape, String> generatePattern) {
        this.innerTarget = innerTarget;
        this.propertyChain = ImmutableList.copyOf(propertyChain.stream().filter( p -> p != null).collect(Collectors.toList()));
        this.generatePattern = generatePattern;
    }


    public static ShapeTarget create(ShapeTarget outerTarget, String property) {
        return create(outerTarget, ResourceFactory.createProperty(property));
    }

    public static ShapeTarget create(ShapeTarget outerTarget, Resource property) {
        return create(outerTarget, Arrays.asList(property));
    }

    public static ShapeTarget create(ShapeTarget innerTarget, List<Resource> propertyChain) {
        switch (innerTarget.getTargetType()) {
            case ClassTarget:
                return new ShapeTargetValueShape(innerTarget, propertyChain, ShapeTargetValueShape::classTargetPattern);
            case NodeTarget:
                return new ShapeTargetValueShape(innerTarget, propertyChain, ShapeTargetValueShape::nodeTargetPattern);
            case ObjectsOfTarget:
                return new ShapeTargetValueShape(innerTarget, propertyChain, ShapeTargetValueShape::objectsOfTargetPattern);
            case SubjectsOfTarget:
                return new ShapeTargetValueShape(innerTarget, propertyChain, ShapeTargetValueShape::subjectsOfTargetPattern);
            case ValueShapeTarget:
                if (innerTarget instanceof ShapeTargetValueShape) {
                    return transformTarget( (ShapeTargetValueShape) innerTarget, propertyChain);
                }  else {
                    throw new IllegalArgumentException("Unsupported target in sh:node");
                }
            default:
                throw new IllegalArgumentException("Unsupported target in sh:node");
        }
    }

    @Override
    public String getUri() {
        return innerTarget.getUri();
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(this);
    }


    private static ShapeTarget transformTarget(ShapeTargetValueShape innerTarget, List<Resource> propertyChain) {
        ImmutableList.Builder<Resource> builder = new ImmutableList.Builder<>();
        builder.addAll(innerTarget.propertyChain);
        builder.addAll(propertyChain);
        return create(innerTarget.innerTarget, builder.build());
    }

    private static String classTargetPattern(ShapeTargetValueShape target) {
        return " [] rdf:type/rdfs:subClassOf* <" + target.getUri() + "> ; " +
                writePropertyChain(target.propertyChain) + "  ?this . ";
    }

    private static String writePropertyChain(List<Resource> propertyChain) {
        return propertyChain.stream()
                .map( p -> "<" + p.getURI() + ">")
                .collect(Collectors.joining("/"));
    }

    private static String nodeTargetPattern(ShapeTargetValueShape target) {
        return " <" + target.getUri() + "> " + writePropertyChain(target.propertyChain) + " ?this . ";
    }

    private static String objectsOfTargetPattern(ShapeTargetValueShape target) {
        return " [] (^<" + target.getUri() + ">)/" + writePropertyChain(target.propertyChain) + " ?this .";
    }

    private static String subjectsOfTargetPattern(ShapeTargetValueShape target) {
        return " [] <" + target.getUri() + ">/" + writePropertyChain(target.propertyChain) + " ?this .";
    }

}
