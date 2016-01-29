package org.aksw.rdfunit.model.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.apache.jena.rdf.model.Resource;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:06 PM
 * @version $Id: $Id
 */
@Value
public class ShapeScopeClassImpl implements ShapeScope {
    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final String scopeClass;

}
