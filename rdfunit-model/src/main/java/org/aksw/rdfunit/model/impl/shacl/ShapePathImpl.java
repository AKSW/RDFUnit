package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathParser;
import org.apache.jena.sparql.path.PathWriter;

/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */

@Builder
@Value
public class ShapePathImpl implements ShapePath {
    @Getter @NonNull private final Path jenaPath;

    public static ShapePath fromString(String propertyPathString) {
        return new ShapePathImpl(
            PathParser.parse(propertyPathString, PrefixMapping.Standard));
    }

    @Override
    public boolean isPredicatePath() {
        return (jenaPath instanceof P_Link);
    }

    @Override
    public String asSparqlPropertyPath() {
        return PathWriter.asString(jenaPath);
    }
}
