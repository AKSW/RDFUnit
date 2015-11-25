package org.aksw.rdfunit.junit;

import java.lang.annotation.*;

@Documented
/**
 * <p>TestInput class.</p>
 *
 * @author Michael Leuthold
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestInput {

    String name() default "";

}
