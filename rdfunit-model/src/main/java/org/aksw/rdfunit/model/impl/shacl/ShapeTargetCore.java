package org.aksw.rdfunit.model.impl.shacl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;

import java.util.function.Function;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeTargetCore implements ShapeTarget {

    @Getter private final ShapeTargetType targetType;
    @Getter private final String uri;
    private final Function<String, String> generatePattern;

    private ShapeTargetCore(ShapeTargetType targetType, String uri, Function<String, String> generatePattern) {
        this.targetType = targetType;
        this.uri = uri;
        this.generatePattern = generatePattern;
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(uri);
    }


    public static ShapeTarget create(@NonNull ShapeTargetType targetType, @NonNull String uri) {
        switch (targetType) {
            case ClassTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::classTargetPattern);
            case NodeTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::nodeTargetPattern);
            case ObjectsOfTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::objectsOfTargetPattern);
            case SubjectsOfTarget:
                return new ShapeTargetCore(targetType, uri, ShapeTargetCore::subjectsOfTargetPattern);
            default:
                throw new IllegalArgumentException("Something wrong with the input");
        }
    }

    private static String classTargetPattern(String uri) {
        return " ?this rdf:type/rdfs:subClassOf* <" + uri + "> . ";
    }

    private static String nodeTargetPattern(String uri) {
        return " BIND (<" + uri + "> AS ?this) . ";
    }
    private static String objectsOfTargetPattern(String uri) {
        return " [] <" + uri + "> ?this .";
    }

    private static String subjectsOfTargetPattern(String uri) {
        return "?this <" + uri + "> [] .";
    }

}
