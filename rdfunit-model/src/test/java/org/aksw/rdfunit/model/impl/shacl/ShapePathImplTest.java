package org.aksw.rdfunit.model.impl.shacl;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */
@Slf4j
public class ShapePathImplTest {

    @Test
    public void testSimplePath() {
        ShapePath p = ShapePathImpl.fromString("<http://example.com/p1>");
        assertThat(p.isPredicatePath()).isTrue();
        log.info("Simple path: {}", p.asSparqlPropertyPath());
    }

    @Test
    public void testComplexPath() {
        ShapePath p = ShapePathImpl.fromString("<http://example.com/p1> / <http://example.com/p2>");
        assertThat(p.isPredicatePath()).isFalse();
        log.info("Complex path: {}", p.asSparqlPropertyPath());
    }

    @Test (expected = Exception.class)
    public void testInvalidPath() {
        ShapePath p = ShapePathImpl.fromString("<http://example.com/p1> / /<http://example.com/p2>");

    }

}