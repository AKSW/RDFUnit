package org.aksw.rdfunit.model.readers.shacl;

import java.util.Set;
import java.util.stream.Collectors;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;


public final class BatchComponentReader {


  private BatchComponentReader() {
  }

  public static BatchComponentReader create() {
    return new BatchComponentReader();
  }

  public Set<Component> getComponentsFromModel(Model model) {

    ComponentReader cr = ComponentReader.create();

    // get all instances of SHACL.ConstraintComponent and return then as Component instances
    return model.listResourcesWithProperty(RDF.type, SHACL.ConstraintComponent)
        .toSet().stream()
        .distinct()
        .map(cr::read)
        .collect(Collectors.toSet());
  }

}
