package org.aksw.rdfunit.model.interfaces;

import java.util.Optional;
import org.aksw.rdfunit.enums.PatternParameterConstraints;

/**
 * Defines a Pattern Parameter.
 *
 * @author Dimitris Kontokostas
 * @since 9 /20/13 2:47 PM
 */
public interface PatternParameter extends Element {


  /**
   * Gets uri.
   *
   * @return the uri
   */
  String getUri();

  /**
   * Gets id.
   *
   * @return the id
   */
  String getId();

  /**
   * Gets constrain.
   *
   * @return the constrain
   */
  PatternParameterConstraints getConstraint();

  /**
   * Gets constraint pattern.
   *
   * @return the constraint pattern
   */
  Optional<String> getConstraintPattern();


}
