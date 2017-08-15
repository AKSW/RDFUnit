package org.aksw.rdfunit.model.helper;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

/**
 * @author Dimitris Kontokostas
 * @since 7/27/17
 */
public final class NodeFormatter {

    private NodeFormatter(){}

    public static String formatNode(RDFNode node) {
        if (node.isURIResource()) {
            return "<" + node.asResource().getURI().trim().replace(" ", "") + ">";
        }
        if (node.isLiteral()){
            Literal value = node.asLiteral();
            String formattedValue = "\"" + value.getLexicalForm() + "\"";
            if (!value.getLanguage().isEmpty()) {
                formattedValue += "@" + value.getLanguage() ;
            }
            if (!value.getDatatypeURI().isEmpty()
                    && !value.getDatatypeURI().endsWith("langString")
                    && !value.getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string")) {
                formattedValue += "^^<" + value.getDatatypeURI() + ">" ;
            }

            return  formattedValue;
        }
        throw new IllegalArgumentException("cannot support blank nodes as targets");
    }
}
