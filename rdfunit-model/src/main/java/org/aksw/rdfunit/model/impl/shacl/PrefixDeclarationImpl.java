package org.aksw.rdfunit.model.impl.shacl;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.apache.jena.rdf.model.Resource;

/**
 * @author Dimitris Kontokostas
 * @since 7/23/17
 */
@Builder
@EqualsAndHashCode(exclude = "element")
@ToString
public class PrefixDeclarationImpl implements PrefixDeclaration {

  @NonNull
  @Getter
  private final Resource element;
  @NonNull
  @Getter
  private final String prefix;
  @NonNull
  @Getter
  private final String namespace;
}
