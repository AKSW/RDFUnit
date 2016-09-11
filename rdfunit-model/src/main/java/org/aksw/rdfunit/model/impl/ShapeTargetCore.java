package org.aksw.rdfunit.model.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.ShapeTarget;

import java.util.Optional;
import java.util.function.Function;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeTargetCore implements ShapeTarget {

    @Getter private final ShapeTargetType targetType;
    @Getter private final Optional<String> uri;
    private final Function<Optional<String>, String> generatePattern;

    private ShapeTargetCore(ShapeTargetType targetType, Optional<String> uri, Function<Optional<String>, String> generatePattern) {
        this.targetType = targetType;
        this.uri = uri;
        this.generatePattern = generatePattern;
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(uri);
    }

    public static ShapeTarget create(@NonNull ShapeTargetType targetType) {
        return create(targetType, Optional.empty());
    }

    public static ShapeTarget create(@NonNull ShapeTargetType targetType, @NonNull String uri) {
        return create(targetType, Optional.of(uri));
    }

    private static ShapeTarget create(ShapeTargetType targetType, Optional<String> uri) {
        switch (targetType) {
            case ClassTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::classTargetPattern);
            case NodeTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::nodeTargetPattern);
//            case AllObjectsScope:
//                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::allObjectsScopePattern);
//            case AllSubjectsScope:
//                return new ShapeScopeCore(scopeType, uri, ShapeScopeCore::allSubjectsScopePattern);
            case ObjectsOfTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::objectsOfTargetPattern);
            case SubjectsOfTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::subjectsOfTargetPattern);
            default:
                throw new IllegalArgumentException("Something wrong with the input");
        }
    }

    private static String classTargetPattern(Optional<String> uri) {
        return " ?this rdf:type/rdfs:subClassOf* <" + uri.get() + "> . ";
    }

    private static String nodeTargetPattern(Optional<String> uri) {
        return " BIND (<" + uri.get() + "> AS ?this) . ";
    }

    private static String objectsOfTargetPattern(Optional<String> uri) {
        return " [] <" + uri.get() + "> ?this .";
    }

    private static String subjectsOfTargetPattern(Optional<String> uri) {
        return "?this <" + uri.get() + "> [] .";
    }

    //private static String allObjectsScopePattern(Optional<String> uri) {
    //    return "[] $shaclAnyPredicate ?this .";
    //}

    //private static String allSubjectsScopePattern(Optional<String> uri) {
    //    return "?this $shaclAnyPredicate [] .";
    //}


}
