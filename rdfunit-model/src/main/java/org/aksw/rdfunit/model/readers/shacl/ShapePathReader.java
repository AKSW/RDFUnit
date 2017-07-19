package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.model.helper.RdfListUtils;
import org.aksw.rdfunit.model.impl.shacl.ShapePathImpl;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.path.P_Link;
import org.apache.jena.sparql.path.Path;
import org.apache.jena.sparql.path.PathFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ShapePathReader implements ElementReader<ShapePath> {

    private ShapePathReader() {}

    public static ShapePathReader create() {
        return new ShapePathReader();
    }

    /** {@inheritDoc} */
    @Override
    public ShapePath read(Resource resource) {
        checkNotNull(resource);

        ShapePathImpl.ShapePathImplBuilder shapePathBuilder = ShapePathImpl.builder();

        shapePathBuilder.element(resource);

        shapePathBuilder.jenaPath(readPath(resource));

        return shapePathBuilder.build();
    }

    private static Path readPath(Resource resource) {
        if (resource.isURIResource()) {
            return new P_Link(resource.asNode());
        }

        if (RdfListUtils.isList(resource)) {
            return RdfListUtils.getListItemsOrEmpty(resource).stream()
                    .filter(RDFNode::isResource)
                    .map(RDFNode::asResource)
                    .map(ShapePathReader::readPath)
                    .reduce(PathFactory::pathSeq)
                    .orElseThrow(() -> new IllegalArgumentException("Sequence path invalid"));
        }
        throw new IllegalArgumentException("Complex paths not yet supported");
    }
}

