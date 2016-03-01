package org.aksw.rdfunit.model.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeScopeType;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

import java.util.Optional;
import java.util.function.Function;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeScopeCore implements ShapeScope{

    @Getter private final ShapeScopeType scopeType;
    @Getter private final Optional<String> uri;
    private final Function<Optional<String>, String> generatePattern;

    public String getPattern() {
        return generatePattern.apply(uri);
    }

    private ShapeScopeCore(ShapeScopeType scopeType, Optional<String> uri, Function<Optional<String>, String> generatePattern) {
        this.scopeType = scopeType;
        this.uri = uri;
        this.generatePattern = generatePattern;
    }

    public static ShapeScope create(@NonNull ShapeScopeType scopeType) {
        return create(scopeType, Optional.empty());
    }

    public static ShapeScope create(@NonNull ShapeScopeType scopeType, @NonNull String uri) {
        return create(scopeType, Optional.of(uri));
    }

    private static ShapeScope create(ShapeScopeType scopeType, Optional<String> uri) {
        switch (scopeType) {
            case ClassScope:
                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::classScopePattern);
            case NodeScope:
                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::nodeScopePattern);
            case AllObjectsScope:
                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::allObjectsScopePattern);
            case AllSubjectsScope:
                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::allSubjectsScopePattern);
            case InversePropertyScope:
                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::inversePropertyScopePattern);
            case PropertyScope:
                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::propertyScopePattern);
            default:
                throw new IllegalArgumentException("Something wrong with the input");
        }
    }

    private static String classScopePattern(Optional<String> uri) {
        return " ?this rdf:type/rdfs:subClassOf* <" + uri.get() + "> . ";
    }

    private static String nodeScopePattern(Optional<String> uri) {
        return " BIND (<" + uri.get() + "> AS ?this) . ";
    }

    private static String inversePropertyScopePattern(Optional<String> uri) {
        return " [] <" + uri.get() + "> ?this .";
    }

    private static String propertyScopePattern(Optional<String> uri) {
        return "?this <" + uri.get() + "> [] .";
    }

    @SuppressWarnings({"SameReturnValue", "UnusedParameters"})
    private static String allObjectsScopePattern(Optional<String> uri) {
        return "[] $shaclAnyPredicate ?this .";
    }

    @SuppressWarnings({"SameReturnValue", "UnusedParameters"})
    private static String allSubjectsScopePattern(Optional<String> uri) {
        return "?this $shaclAnyPredicate [] .";
    }


}
