package org.aksw.rdfunit.junit;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestInput {

    String name() default "";

}
