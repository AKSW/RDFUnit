package org.aksw.rdfunit.model.impl.shacl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.RDFNode;

import java.util.function.Function;

import static org.aksw.rdfunit.model.helper.NodeFormatter.formatNode;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode
public class ShapeTargetCore implements ShapeTarget {

    @Getter private final ShapeTargetType targetType;
    @Getter private final RDFNode node;
    private final Function<RDFNode, String> generatePattern;

    private ShapeTargetCore(ShapeTargetType targetType, RDFNode node, Function<RDFNode, String> generatePattern) {
        this.targetType = targetType;
        this.node = node;
        this.generatePattern = generatePattern;
    }

    @Override
    public String getPattern() {
        return generatePattern.apply(node);
    }


    public static ShapeTarget create(@NonNull ShapeTargetType targetType, @NonNull RDFNode node) {
        switch (targetType) {
            case ClassTarget:
                return new ShapeTargetCore(targetType, node, ShapeTargetCore::classTargetPattern);
            case NodeTarget:
                return new ShapeTargetCore(targetType, node, ShapeTargetCore::nodeTargetPattern);
            case ObjectsOfTarget:
                return new ShapeTargetCore(targetType, node, ShapeTargetCore::objectsOfTargetPattern);
            case SubjectsOfTarget:
                return new ShapeTargetCore(targetType, node, ShapeTargetCore::subjectsOfTargetPattern);
            default:
                throw new IllegalArgumentException("Something wrong with the input");
        }
    }

    private static String classTargetPattern(RDFNode node) {
        return " ?this rdf:type/rdfs:subClassOf* " + formatNode(node) + " . ";
    }

    private static String nodeTargetPattern(RDFNode node) {
        return " BIND (" + formatNode(node) + " AS ?this) . ";
    }
    private static String objectsOfTargetPattern(RDFNode node) {
        return " [] " + formatNode(node) + " ?this .";
    }

    private static String subjectsOfTargetPattern(RDFNode node) {
        return "?this " + formatNode(node) + " [] .";
    }

}
