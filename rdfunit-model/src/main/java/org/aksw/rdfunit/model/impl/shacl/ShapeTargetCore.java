package org.aksw.rdfunit.model.impl.shacl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.ext.com.google.common.collect.ImmutableSet;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Set;
import java.util.function.Function;

@ToString(exclude = "generatePattern")
@EqualsAndHashCode(of = {"targetType", "node"})
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

    @Override
    public Set<Resource> getRelatedOntologyResources() {
        if (node.isURIResource() && !targetType.equals(ShapeTargetType.NodeTarget)) {
            return ImmutableSet.of(node.asResource());
        } else {
            return ImmutableSet.of();
        }

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
        return getBindedSparql(" ?this <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>/<http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?target . ", node);
    }

    private static String nodeTargetPattern(RDFNode node) {
        return getBindedSparql(" BIND (?target AS ?this) . ", node);
    }
    private static String objectsOfTargetPattern(RDFNode node) {
        return getBindedSparql(" [] ?target ?this .", node);
    }

    private static String subjectsOfTargetPattern(RDFNode node) {
        return getBindedSparql("?this ?target [] .", node);
    }

    static String getBindedSparql(String sparql, RDFNode node) {
        ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString("ASK{" +sparql+ "}");
        parameterizedSparqlString.setParam("target", node);
        String queryPattern = parameterizedSparqlString
                .toString()
                .trim()
                .replace("ASK{", "");
        // remove last '}'
        return queryPattern.substring(0, queryPattern.length()-1);
    }

}
