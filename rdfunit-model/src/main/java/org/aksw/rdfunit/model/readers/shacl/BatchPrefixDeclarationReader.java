package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.Set;
import java.util.stream.Collectors;


public final class BatchPrefixDeclarationReader {

    private BatchPrefixDeclarationReader(){}

    public static BatchPrefixDeclarationReader create() { return new BatchPrefixDeclarationReader();}

    public Set<PrefixDeclaration> getPrefixDeclarations(Resource resource) {

        // TODO handle owl:imports
        return resource.listProperties(SHACL.declare)
                .toSet().stream()
                .distinct()
                .map(Statement::getObject)
                .filter(RDFNode::isResource)
                .map(RDFNode::asResource)
                .map(r -> PrefixDeclarationReader.create().read(r))
                .collect(Collectors.toSet());
    }

}
