package org.aksw.rdfunit.model.helper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * Holds a select variable mapping
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 4:39 PM
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SelectVar {

  @Getter
  @NonNull
  private final String name;
  @Getter
  @NonNull
  private final String label;

  public static SelectVar create(String name) {
    return new SelectVar(name, name);
  }

  public static SelectVar create(String name, String label) {
    return new SelectVar(name, label);
  }

  public String asString() {
    if (isLabeled()) {
      return " ( ?" + name + " AS ?" + label + " ) ";
    } else {
      return " ?" + name + " ";
    }
  }

  private boolean isLabeled() {
    return !label.equals(name);
  }


}
