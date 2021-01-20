package org.aksw.rdfunit.model.readers.shacl;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aksw.rdfunit.model.impl.shacl.SparqlValidatorImpl;
import org.aksw.rdfunit.model.interfaces.shacl.Validator;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;


public class SparqlValidatorReader implements ElementReader<Validator> {

  private SparqlValidatorReader() {
  }

  public static SparqlValidatorReader create() {
    return new SparqlValidatorReader();
  }

  @Override
  public Validator read(Resource resource) {
    checkNotNull(resource);

    SparqlValidatorImpl.SparqlValidatorImplBuilder validatorBuilder = SparqlValidatorImpl.builder();

    validatorBuilder.element(resource);

    // get message
    for (Statement smt : resource.listProperties(SHACL.message).toList()) {
      validatorBuilder.message(smt.getObject().asLiteral());
    }

    // get prefixes
    for (Statement smt : resource.listProperties(SHACL.prefixes).toList()) {
      RDFNode obj = smt.getObject();
      if (obj.isResource()) {
        validatorBuilder.prefixDeclarations(
            BatchPrefixDeclarationReader.create().getPrefixDeclarations(obj.asResource()));
      }
    }

    //default sparql query
    for (Statement smt : resource.listProperties(SHACL.select).toList()) {
      validatorBuilder.sparqlQuery(smt.getObject().asLiteral().getLexicalForm());
    }

    return validatorBuilder.build();
  }
}
