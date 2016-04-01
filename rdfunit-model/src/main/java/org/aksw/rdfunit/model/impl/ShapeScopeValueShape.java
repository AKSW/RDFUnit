package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeScopeType;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class
ShapeScopeValueShape implements ShapeScope{

    @Getter private final ShapeScopeType scopeType = ShapeScopeType.ValueShapeScope;
    private final ImmutableList<Resource> propertyChain;
    private final ShapeScope outerScope;
    private final Function<ShapeScopeValueShape, String> generatePattern;

    private ShapeScopeValueShape(ShapeScope outerScope, List<Resource> propertyChain, Function<ShapeScopeValueShape, String> generatePattern) {
        this.outerScope = outerScope;
        this.propertyChain = ImmutableList.copyOf(propertyChain);
        this.generatePattern = generatePattern;
    }

    public static ShapeScope create(ShapeScope outerScope, List<Resource> propertyChain) {
        switch (outerScope.getScopeType()) {
            case ClassScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::classScopePattern);
            case NodeScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::nodeScopePattern);
            case AllObjectsScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::allObjectsScopePattern);
            case AllSubjectsScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::allSubjectsScopePattern);
            case InversePropertyScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::inversePropertyScopePattern);
            case PropertyScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::propertyScopePattern);
            case ValueShapeScope:
                if (outerScope instanceof ShapeScopeValueShape) {
                    return transformScope((ShapeScopeValueShape) outerScope, propertyChain);
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


    private static ShapeScope transformScope(ShapeScopeValueShape outerScope, List<Resource> propertyChain) {
        ImmutableList.Builder<Resource> builder = new ImmutableList.Builder<>();
        builder.addAll(propertyChain);
        builder.addAll(outerScope.propertyChain);
        return create(outerScope.outerScope, builder.build());
    }

    private static String classScopePattern(ShapeScopeValueShape scope) {
        return " [] rdf:type/rdfs:subClassOf* <" + scope.getUri().get() + "> ; " +
                writePropertyChain(scope.propertyChain) + "  ?this . ";
    }

    private static String writePropertyChain(List<Resource> propertyChain) {
        return propertyChain.stream()
                .map( p -> "<" + p.getURI() + ">")
                .collect(Collectors.joining("/"));
    }

    private static String nodeScopePattern(ShapeScopeValueShape scope) {
        return " <" + scope.getUri().get() + "> " + writePropertyChain(scope.propertyChain) + " ?this . ";
    }

    private static String inversePropertyScopePattern(ShapeScopeValueShape scope) {
        return " [] (^<" + scope.getUri().get() + ">)/" + writePropertyChain(scope.propertyChain) + " ?this .";
    }

    private static String propertyScopePattern(ShapeScopeValueShape scope) {
        return " [] <" + scope.getUri().get() + ">/" + writePropertyChain(scope.propertyChain) + " ?this .";
    }

    private static String allObjectsScopePattern(ShapeScopeValueShape scope) {
        return " [] ^$shaclAnyPredicate <" + scope.getUri().get() + "> ; " +
                writePropertyChain(scope.propertyChain) + "  ?this . ";
    }

    private static String allSubjectsScopePattern(ShapeScopeValueShape scope) {
        return " [] $shaclAnyPredicate <" + scope.getUri().get() + "> ; " +
                writePropertyChain(scope.propertyChain) + "  ?this . ";
    }


}
