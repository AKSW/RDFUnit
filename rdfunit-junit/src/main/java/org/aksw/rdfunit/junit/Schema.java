package org.aksw.rdfunit.junit;

import java.lang.annotation.*;

@Documented
/**
 * <p>Schema class.</p>
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Schema {

    /**
     * @return the URI defining the target ontology
     */
    String uri();

}
