package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.interfaces.Shape;
import org.apache.jena.rdf.model.Resource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:15 PM
 * @version $Id: $Id
 */
public final class ShapeReader implements ElementReader<Shape> {

    private ShapeReader() {
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternReader} object.
     */
    public static ShapeReader create() {
        return new ShapeReader();
    }

    /** {@inheritDoc} */
    @Override
    public Shape read(Resource resource) {
        checkNotNull(resource);

        return null;


    }
}

