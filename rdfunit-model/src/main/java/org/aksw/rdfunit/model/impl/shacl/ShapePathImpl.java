package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
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

        Model model = ModelFactory.createDefaultModel();
        if (path instanceof P_Link) {
            Node node = ((P_Link) path).getNode();
            if (node.isURI()) {
                return model.createResource(node.getURI());
            }
        }

        if (path instanceof P_Seq) {
            Resource left  = createPath(((P_Seq) path).getLeft());
            Resource right = createPath(((P_Seq) path).getRight());
            model.add(left.getModel()).add(right.getModel());

            return model.createList(Arrays.asList(left, right).iterator());
        }

        if (path instanceof P_Alt) {
            Resource left  = createPath(((P_Alt) path).getLeft());
            Resource right = createPath(((P_Alt) path).getRight());
            model.add(left.getModel()).add(right.getModel());

            Resource r = model.createResource();
            r.addProperty(
                    SHACL.alternativePath,
                    model.createList(Arrays.asList(left, right).iterator()));
            return r;
        }

        if (path instanceof P_Inverse) {
            return getResourceFromPath1(path, SHACL.inversePath, model);
        }

        if (path instanceof P_OneOrMore1 || path instanceof P_OneOrMoreN) {
            return getResourceFromPath1(path, SHACL.oneOrMorePath, model);
        }

        if (path instanceof P_ZeroOrMore1 || path instanceof P_ZeroOrMoreN) {
            return getResourceFromPath1(path, SHACL.zeroOrMorePath, model);
        }
        if (path instanceof P_ZeroOrOne) {
            return getResourceFromPath1(path, SHACL.zeroOrOnePath, model);
        }

        throw new IllegalStateException("Cannot serialize unknown SHACL path");
    }

    private Resource getResourceFromPath1(Path path, Property property, Model model) {

        Path invPath = ((P_Path1) path).getSubPath();
        Resource r = model.createResource();
        r.addProperty(
                property,
                createPath(invPath));
        return r;
    }
}
