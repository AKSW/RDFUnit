package org.aksw.rdfunit.junit;

import java.lang.annotation.*;

@Documented
/**
 * @author Michael Leuthold

 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Schema {

    /**
     * @return the URI defining the target ontology
     */
    String uri();

}
