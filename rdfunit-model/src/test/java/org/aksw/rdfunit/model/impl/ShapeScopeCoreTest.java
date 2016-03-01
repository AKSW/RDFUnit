package org.aksw.rdfunit.model.impl;

import org.aksw.rdfunit.enums.ShapeScopeType;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 5/2/2016 2:00 μμ
 */
public class ShapeScopeCoreTest {

    @Test(expected=NullPointerException.class)
    public void testCreateNullType() {
        ShapeScopeCore.create(null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateNullValue() {
        ShapeScopeCore.create(ShapeScopeType.AllObjectsScope, null);
    }

    @Test
    public void testPatternUnique() {

        List<String> scopePatterns = Arrays.stream(ShapeScopeType.values() )
                .filter( sct -> !sct.equals(ShapeScopeType.ValueShapeScope))
                .map( s -> ShapeScopeCore.create(s, "http://example.com"))
                .map(ShapeScope::getPattern)
                .collect(Collectors.toList());

        // each scope results in different pattern
        assertThat(scopePatterns.size())
                .isEqualTo(new HashSet<>(scopePatterns).size());


    }

    @Test
    public void testScopeContainsUri() {

        String uri = "http://example.com";
        Arrays.stream(ShapeScopeType.values() )
                .filter( sct -> !sct.equals(ShapeScopeType.ValueShapeScope))
                .map( s -> ShapeScopeCore.create(s, uri))
                .filter( s -> s.getScopeType().hasArgument())
                .forEach( s -> {
                    assertThat(s.getPattern()).contains(uri);
                    assertThat(s.getUri().get()).isEqualTo(uri);
                });

    }

}