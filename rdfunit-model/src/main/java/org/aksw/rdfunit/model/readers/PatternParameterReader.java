package org.aksw.rdfunit.model.readers;

import static com.google.common.base.Preconditions.checkNotNull;

import org.aksw.rdfunit.model.impl.PatternParameterImpl;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class PatternParameterReader implements ElementReader<PatternParameter> {

  private PatternParameterReader() {
  }

  public static PatternParameterReader create() {
    return new PatternParameterReader();
  }


  @Override
  public PatternParameter read(Resource resource) {
    checkNotNull(resource);

    PatternParameterImpl.Builder parameterBuilder = new PatternParameterImpl.Builder();

    parameterBuilder.setElement(resource);

    // get ID
    for (Statement smt : resource.listProperties(DCTerms.identifier).toList()) {
      parameterBuilder.setID(smt.getObject().asLiteral().getLexicalForm());
    }

    // parameter constraints
    for (Statement smt : resource.listProperties(RDFUNITv.parameterConstraint).toList()) {
      parameterBuilder.setPatternParameterConstraints(smt.getObject().asResource().getURI());
    }

    //constraint pattern
    for (Statement smt : resource.listProperties(RDFUNITv.constraintPattern).toList()) {
      parameterBuilder.setConstraintPattern(smt.getObject().asLiteral().getLexicalForm());
    }

    return parameterBuilder.build();
  }
}
