package org.aksw.rdfunit.model.impl;

import com.hp.hpl.jena.rdf.model.Resource;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.ShapeScope;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:06 PM
 * @version $Id: $Id
 */
@Value
public class ShapeScopeClass implements ShapeScope {
    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final String scopeClass;

}
