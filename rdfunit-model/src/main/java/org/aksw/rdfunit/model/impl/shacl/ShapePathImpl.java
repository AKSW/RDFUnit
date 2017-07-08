package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathWriter;

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
}
