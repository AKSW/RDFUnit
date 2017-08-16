package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author Dimitris Kontokostas
 * @since 8/16/17
 */
@Value
public class MessagePrebinding {
    @NonNull private final String message;
    @NonNull private final Shape shape;

    public String applyBindings() {
        return applyBindings(ImmutableMap.of());
    }

    public String applyBindings(Map<ComponentParameter, RDFNode> bindings) {

        String newMessage = message;
        if (shape.isPropertyShape()) {
            newMessage.replaceAll(getVariablePattern("PATH"), Matcher.quoteReplacement(shape.getPath().get().asSparqlPropertyPath()));
        }

        for (Map.Entry<ComponentParameter, RDFNode> entry : bindings.entrySet()) {
            String value = formatRdfValue(entry.getValue());
            newMessage.replaceAll(
                    getVariablePattern(entry.getKey().getParameterName()), Matcher.quoteReplacement(value));
        }
        // FIXME add fixed bindings e.g. $shape etc

        return newMessage;
    }

    private String getVariablePattern(String variableName) {
        return "\\{?[\\$\\?]" + variableName + "\\}?";
    }


    private String formatRdfValue(RDFNode value) {
        if (RdfListUtils.isList(value)) {
            return RdfListUtils.getListItemsOrEmpty(value).stream().map(NodeFormatter::formatNode).collect(Collectors.joining(" , "));
        } else {
            return NodeFormatter.formatNode(value);
        }
    }

}
