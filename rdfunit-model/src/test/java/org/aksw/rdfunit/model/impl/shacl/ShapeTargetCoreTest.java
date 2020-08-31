package org.aksw.rdfunit.model.impl.shacl;

import static org.aksw.rdfunit.model.helper.NodeFormatter.formatNode;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 5/2/2016 2:00 μμ
 */
public class ShapeTargetCoreTest {

  @Test(expected = NullPointerException.class)
  public void testCreateNullType() {
    ShapeTargetCore.create(null, null);
  }

  @Test
  public void testPatternUnique() {
    List<String> targetPatterns = Arrays.stream(ShapeTargetType.values())
        .filter(sct -> !sct.equals(ShapeTargetType.ValueShapeTarget))
        .map(s -> ShapeTargetCore.create(s, ResourceFactory.createResource("http://example.com")))
        .map(ShapeTarget::getPattern)
        .collect(Collectors.toList());

    // each target results in different pattern
    assertThat(targetPatterns.size())
        .isEqualTo(new HashSet<>(targetPatterns).size());
  }

  @Test
  public void testTargetContainsUri() {
    Resource uri = ResourceFactory.createResource("http://example.com");
    Arrays.stream(ShapeTargetType.values())
        .filter(sct -> !sct.equals(ShapeTargetType.ValueShapeTarget))
        .map(s -> ShapeTargetCore.create(s, uri))
        .filter(s -> s.getTargetType().hasArgument())
        .forEach(s -> {
          assertThat(s.getPattern()).contains(formatNode(uri));
          assertThat(s.getNode()).isEqualTo(uri);
        });
  }

  @Test
  public void testTargetNode() {
    ShapeTarget st = ShapeTargetCore
        .create(ShapeTargetType.NodeTarget, ResourceFactory.createResource("http://a.com"));
    assertThat("BIND (<http://a.com> AS ?this) .")
        .isEqualTo(st.getPattern().trim());
  }

  @Test
  public void testSubjectOf() {
    ShapeTarget st = ShapeTargetCore
        .create(ShapeTargetType.SubjectsOfTarget, ResourceFactory.createResource("http://p.com"));
    assertThat("?this <http://p.com> [] .")
        .isEqualTo(st.getPattern().trim());
  }

}