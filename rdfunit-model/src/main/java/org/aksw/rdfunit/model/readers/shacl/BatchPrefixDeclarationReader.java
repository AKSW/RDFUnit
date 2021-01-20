package org.aksw.rdfunit.model.readers.shacl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.rdf.model.Resource;


public final class BatchPrefixDeclarationReader {

  private static final String sparqlQuery =
      "SELECT distinct ?declare where {?resource (<http://www.w3.org/2002/07/owl#imports>*/<http://www.w3.org/ns/shacl#declare>) ?declare }";

  private BatchPrefixDeclarationReader() {
  }

  public static BatchPrefixDeclarationReader create() {
    return new BatchPrefixDeclarationReader();
  }

  public Set<PrefixDeclaration> getPrefixDeclarations(Resource resource) {

    ImmutableSet.Builder<PrefixDeclaration> prefixes = ImmutableSet.builder();
    try (
        QueryExecutionFactory qef = new QueryExecutionFactoryModel(resource.getModel());
        QueryExecution qe = qef.createQueryExecution(sparqlQuery)) {
      qe.execSelect().forEachRemaining(solution -> {
        PrefixDeclaration pr = PrefixDeclarationReader.create()
            .read(solution.getResource("declare"));
        prefixes.add(pr);
      });

    } catch (Exception e) {
      e.printStackTrace();
    }

    return prefixes.build();
  }

}
