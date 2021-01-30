package org.aksw.rdfunit.model.readers.shacl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aksw.rdfunit.model.impl.shacl.PrefixDeclarationImpl;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;


public class PrefixDeclarationReader implements ElementReader<PrefixDeclaration> {

  private PrefixDeclarationReader() {
  }

  public static PrefixDeclarationReader create() {
    return new PrefixDeclarationReader();
  }

  @Override
  public PrefixDeclaration read(Resource resource) {
    checkNotNull(resource);

    PrefixDeclarationImpl.PrefixDeclarationImplBuilder prefixDeclarationImplBuilder = PrefixDeclarationImpl
        .builder();

    prefixDeclarationImplBuilder.element(resource);

    for (Statement smt : resource.listProperties(SHACL.prefix).toList()) {
      prefixDeclarationImplBuilder.prefix(smt.getObject().asLiteral().getLexicalForm());
    }

    for (Statement smt : resource.listProperties(SHACL.prefixNamespace).toList()) {
      prefixDeclarationImplBuilder.namespace(smt.getObject().asLiteral().getLexicalForm());
    }

    return prefixDeclarationImplBuilder.build();
  }
}
