package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:23 PM
 * @version $Id: $Id
 */
public final class ShapeScopeReader implements ElementReader<ShapeScope> {

    private ShapeScopeReader() {
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternReader} object.
     */
    public static ShapeScopeReader create() {
        return new ShapeScopeReader();
    }

    /** {@inheritDoc} */
    @Override
    public ShapeScope read(Resource resource) {
        checkNotNull(resource);

        return null;

    }
}

