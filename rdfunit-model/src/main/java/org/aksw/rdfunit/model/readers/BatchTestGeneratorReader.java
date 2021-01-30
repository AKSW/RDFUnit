package org.aksw.rdfunit.model.readers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM
 */
public final class BatchTestGeneratorReader {

  private BatchTestGeneratorReader() {
  }

  public static BatchTestGeneratorReader create() {
    return new BatchTestGeneratorReader();
  }

  public Collection<TestGenerator> getTestGeneratorsFromModel(Model model) {
    return getTestGeneratorsFromResourceList(
        model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList()
    );
  }

  public Collection<TestGenerator> getTestGeneratorsFromResourceList(List<Resource> resources) {
    return resources.stream()
        .map(resource -> TestGeneratorReader.create().read(resource))
        .collect(Collectors.toList());

  }
}
