package org.aksw.rdfunit.model.impl;

import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.ShapeTarget;
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
public class ShapeTargetCoreTest {

    @Test(expected=NullPointerException.class)
    public void testCreateNullType() {
        ShapeTargetCore.create(null);
    }

//    @Test(expected=NullPointerException.class)
//    public void testCreateNullValue() {
//        ShapeTargetCore.create(ShapeTargetType.AllObjectsTarget, null);
//    }

    @Test
    public void testPatternUnique() {

        List<String> targetPatterns = Arrays.stream(ShapeTargetType.values() )
                .filter( sct -> !sct.equals(ShapeTargetType.ValueShapeTarget))
                .map( s -> ShapeTargetCore.create(s, "http://example.com"))
                .map(ShapeTarget::getPattern)
                .collect(Collectors.toList());

        // each target results in different pattern
        assertThat(targetPatterns.size())
                .isEqualTo(new HashSet<>(targetPatterns).size());


    }

    @Test
    public void testTargetContainsUri() {

        String uri = "http://example.com";
        Arrays.stream(ShapeTargetType.values() )
                .filter( sct -> !sct.equals(ShapeTargetType.ValueShapeTarget))
                .map( s -> ShapeTargetCore.create(s, uri))
                .filter( s -> s.getTargetType().hasArgument())
                .forEach( s -> {
                    assertThat(s.getPattern()).contains(uri);
                    assertThat(s.getUri().get()).isEqualTo(uri);
                });

    }

}