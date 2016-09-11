package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.ShapeTarget;
import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeTargetValueShape implements ShapeTarget {

    @Getter private final ShapeTargetType targetType = ShapeTargetType.ValueShapeTarget;
    private final ImmutableList<Resource> propertyChain;
    private final ShapeTarget outerScope;
    private final Function<ShapeTargetValueShape, String> generatePattern;

    private ShapeTargetValueShape(ShapeTarget outerScope, List<Resource> propertyChain, Function<ShapeTargetValueShape, String> generatePattern) {
        this.outerScope = outerScope;
        this.propertyChain = ImmutableList.copyOf(propertyChain);
        this.generatePattern = generatePattern;
    }

    public static ShapeTarget create(ShapeTarget outerScope, List<Resource> propertyChain) {
        switch (outerScope.getTargetType()) {
            case ClassTarget:
                return new ShapeTargetValueShape(outerScope, propertyChain, ShapeTargetValueShape::classTargetPattern);
            case NodeTarget:
                return new ShapeTargetValueShape(outerScope, propertyChain, ShapeTargetValueShape::nodeTargetPattern);
//            case AllObjectsScope:
//                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::allObjectsScopePattern);
//            case AllSubjectsScope:
//                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::allSubjectsScopePattern);
            case ObjectsOfTarget:
                return new ShapeTargetValueShape(outerScope, propertyChain, ShapeTargetValueShape::objectsOfTargetPattern);
            case SubjectsOfTarget:
                return new ShapeTargetValueShape(outerScope, propertyChain, ShapeTargetValueShape::subjectsOfTargetPattern);
            case ValueShapeTarget:
                if (outerScope instanceof ShapeTargetValueShape) {
                    return transformScope((ShapeTargetValueShape) outerScope, propertyChain);
                }
            default:
                throw new IllegalArgumentException("Unsupported scope in sh:valueShape");
        }
    }

    @Override
    public Optional<String> getUri() {
        return outerScope.getUri();
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(this);
    }


    private static ShapeTarget transformScope(ShapeTargetValueShape outerScope, List<Resource> propertyChain) {
        ImmutableList.Builder<Resource> builder = new ImmutableList.Builder<>();
        builder.addAll(propertyChain);
        builder.addAll(outerScope.propertyChain);
        return create(outerScope.outerScope, builder.build());
    }

    private static String classTargetPattern(ShapeTargetValueShape scope) {
        return " [] rdf:type/rdfs:subClassOf* <" + scope.getUri().get() + "> ; " +
                writePropertyChain(scope.propertyChain) + "  ?this . ";
    }

    private static String writePropertyChain(List<Resource> propertyChain) {
        return propertyChain.stream()
                .map( p -> "<" + p.getURI() + ">")
                .collect(Collectors.joining("/"));
    }

    private static String nodeTargetPattern(ShapeTargetValueShape scope) {
        return " <" + scope.getUri().get() + "> " + writePropertyChain(scope.propertyChain) + " ?this . ";
    }

    private static String objectsOfTargetPattern(ShapeTargetValueShape scope) {
        return " [] (^<" + scope.getUri().get() + ">)/" + writePropertyChain(scope.propertyChain) + " ?this .";
    }

    private static String subjectsOfTargetPattern(ShapeTargetValueShape scope) {
        return " [] <" + scope.getUri().get() + ">/" + writePropertyChain(scope.propertyChain) + " ?this .";
    }

    //private static String allObjectsScopePattern(ShapeTargetValueShape scope) {
    //    return " [] ^$shaclAnyPredicate <" + scope.getUri().get() + "> ; " +
    //            writePropertyChain(scope.propertyChain) + "  ?this . ";
    //}

    //private static String allSubjectsScopePattern(ShapeTargetValueShape scope) {
    //    return " [] $shaclAnyPredicate <" + scope.getUri().get() + "> ; " +
    //            writePropertyChain(scope.propertyChain) + "  ?this . ";
    //}


}
