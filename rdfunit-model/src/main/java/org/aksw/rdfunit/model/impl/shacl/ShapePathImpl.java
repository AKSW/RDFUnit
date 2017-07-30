package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.helper.RdfListUtils;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.path.*;

import java.util.Arrays;

/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */

@Builder
@Value
public class ShapePathImpl implements ShapePath {

    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final Path jenaPath;

    @Override
    public boolean isPredicatePath() {
        return (jenaPath instanceof P_Link);
    }

    @Override
    public String asSparqlPropertyPath() {
        return PathWriter.asString(jenaPath);
    }

    @Override
    public Resource getPathAsRdf() {

        return createPath(jenaPath);
    }

    private Resource createPath(Path path) {

        if (path instanceof P_Link) {
            Node node = ((P_Link) path).getNode();
            if (node.isURI()) {
                return ResourceFactory.createResource(node.getURI());
            }
        }

        if (path instanceof P_Seq) {
            Resource left  = createPath(((P_Seq) path).getLeft());
            Resource right = createPath(((P_Seq) path).getRight());

            return RdfListUtils.createListOfNodes(Arrays.asList(left, right));
        }

        if (path instanceof P_Alt) {
            Resource left  = createPath(((P_Alt) path).getLeft());
            Resource right = createPath(((P_Alt) path).getRight());

            Resource r = ModelFactory.createDefaultModel().createResource();
            r.addProperty(
                    SHACL.alternativePath,
                    RdfListUtils.createListOfNodes(Arrays.asList(left, right)));
            return r;
        }

        if (path instanceof P_Inverse) {
            return getResourceFromPath1(path, SHACL.inversePath);
        }

        if (path instanceof P_OneOrMore1 || path instanceof P_OneOrMoreN) {
            return getResourceFromPath1(path, SHACL.oneOrMorePath);
        }

        if (path instanceof P_ZeroOrMore1 || path instanceof P_ZeroOrMoreN) {
            return getResourceFromPath1(path, SHACL.zeroOrMorePath);
        }
        if (path instanceof P_ZeroOrOne) {
            return getResourceFromPath1(path, SHACL.zeroOrOnePath);
        }
        return getElement();
    }

    private Resource getResourceFromPath1(Path path, Property property) {

        Path invPath = ((P_Path1) path).getSubPath();
        Resource r = ModelFactory.createDefaultModel().createResource();
        r.addProperty(
                property,
                createPath(invPath));
        return r;
    }
}
