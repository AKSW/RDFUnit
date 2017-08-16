package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.model.helper.NodeFormatter;
import org.aksw.rdfunit.model.helper.RdfListUtils;
import org.aksw.rdfunit.model.interfaces.Element;
import org.aksw.rdfunit.vocabulary.RDFUNIT_SHACL_EXT;
import org.apache.jena.rdf.model.*;

import java.util.Optional;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

/**
 * Interface for SHACL Component parameters
 *
 * @author Dimitris Kontokostas
 * @since 6 /17/15 3:15 PM
 */
public interface ComponentParameter extends Element {

    boolean isOptional();
    default boolean isRequired() {return !isOptional();}

    Property getPredicate();

    Optional<RDFNode> getDefaultValue();

    default String getParameterName() {return getPredicate().getLocalName();}

    default boolean isParameterForRawStringReplace() {
        return getElement().hasLiteral(RDFUNIT_SHACL_EXT.doRawStringReplace, true);
    }

    default Optional<Resource> getValueFormatter() {
        Statement smt = getElement().getProperty(RDFUNIT_SHACL_EXT.formatParameterValueAs);
        if (smt != null && smt.getObject().isResource()) {
            return Optional.of(smt.getObject().asResource());
        } else {
            return Optional.empty();
        }
    }

    default RDFNode getBindingValue(RDFNode node, Shape shape) {
        // when non formatting query exists return the same value
        if (!getValueFormatter().isPresent()) {
            return node;
        }
        Resource formatter = getValueFormatter().get();
        if (RDFUNIT_SHACL_EXT.FormatListCommaSeparated.equals(formatter)) {
            return ResourceFactory.createStringLiteral(
                    RdfListUtils.getListItemsOrEmpty(node).stream()
                            .map(NodeFormatter::formatNode)
                            .collect(Collectors.joining(" , "))
            );
        }
        if (RDFUNIT_SHACL_EXT.FormatListSpaceSeparated.equals(formatter)) {
            return ResourceFactory.createStringLiteral(
                    RdfListUtils.getListItemsOrEmpty(node).stream()
                            .map(NodeFormatter::formatNode)
                            .collect(Collectors.joining("  "))
            );
        }

        throw new UnknownFormatConversionException("Cannot find formatter for " + formatter);
    }

}
