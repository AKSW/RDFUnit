package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeScopeType;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.apache.jena.rdf.model.Property;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeScopeValueShape implements ShapeScope{

    @Getter private final ShapeScopeType scopeType = ShapeScopeType.ValueShapeScope;
    private final ImmutableList<Property> propertyChain;
    private final ShapeScope outerScope;
    private final Function<ShapeScopeValueShape, String> generatePattern;

    @Override
    public Optional<String> getUri() {
        return outerScope.getUri();
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(this);
    }

    private ShapeScopeValueShape(ShapeScope outerScope, List<Property> propertyChain, Function<ShapeScopeValueShape, String> generatePattern) {
        this.outerScope = outerScope;
        this.propertyChain = ImmutableList.copyOf(propertyChain);
        this.generatePattern = generatePattern;
    }

    public static ShapeScope create(ShapeScope outerScope, List<Property> propertyChain) {
        switch (outerScope.getScopeType()) {
            case ClassScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::classScopePattern);
            case NodeScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::nodeScopePattern);
           /* case AllObjectsScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::allObjectsScopePattern);
            case AllSubjectsScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::allSubjectsScopePattern);
            case InversePropertyScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::inversePropertyScopePattern);
            case PropertyScope:
                return new ShapeScopeValueShape(outerScope, propertyChain, ShapeScopeValueShape::propertyScopePattern); */
            case ValueShapeScope:
                if (outerScope instanceof ShapeScopeValueShape) {
                    ShapeScopeValueShape transformScope = (ShapeScopeValueShape) outerScope;
                    ImmutableList.Builder<Property> builder = new ImmutableList.Builder<>();
                    builder.addAll(propertyChain);
                    builder.addAll(transformScope.propertyChain);
                    return create(transformScope.outerScope, builder.build());
                }
        }

        throw new IllegalArgumentException("Something wrong with the input");
    }

    private static String classScopePattern(ShapeScopeValueShape scope) {
        return " [] rdf:type/rdfs:subClassOf* <" + scope.getUri().get() + "> ; " +
                writePropertyChain(scope.propertyChain) + "  ?this . ";
    }

    private static String writePropertyChain(List<Property> propertyChain) {
        return propertyChain.stream()
                .map( p -> "<" + p.getURI() + ">")
                .collect(Collectors.joining("/"));
    }

    private static String nodeScopePattern(ShapeScopeValueShape scope) {
        return " <" + scope.getUri().get() + "> " + writePropertyChain(scope.propertyChain) + " ?this) . ";
    }
   /*
    private static String inversePropertyScopePattern(ShapeScopeValueShape scope) {
        return " [] <" + uri.get() + "> ?this .";
    }

    private static String propertyScopePattern(ShapeScopeValueShape scope) {
        return "?this <" + uri.get() + "> [] .";
    }

    private static String allObjectsScopePattern(ShapeScopeValueShape scope) {
        return "[] $shaclAnyPredicate ?this .";
    }

    private static String allSubjectsScopePattern(ShapeScopeValueShape scope) {
        return "?this $shaclAnyPredicate [] .";
    } */


}
