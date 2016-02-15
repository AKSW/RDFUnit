package org.aksw.rdfunit.model.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.model.interfaces.PropertyConstraint;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 11/2/2016 9:28 πμ
 */
@Builder
public class TemplateRegistry {
    @Getter
    @NonNull
    @Singular
    private final Set<ShaclPropertyConstraintTemplate> shaclCoreTemplates;


    public Set<PropertyConstraint> generatePropertyConstraints(PropertyValuePairSet propertyValuePairSet) {

        return shaclCoreTemplates.stream()
                //get all patterns that can bind to the input
                .filter(p -> p.getArguments().stream().allMatch(a -> a.canBind(propertyValuePairSet)))
                // create bindings for each and map tp ShaclBindingPattern
                .map(p -> {
                    ShaclPropertyConstraintInstance.ShaclPropertyConstraintInstanceBuilder builder = ShaclPropertyConstraintInstance.builder();

                    p.getArguments().stream().forEach( arg -> builder.binding(arg, arg.getBindFromValues(propertyValuePairSet).get()));

                    return builder.template(p).build();

                }).collect(Collectors.toSet());

    }

    public static TemplateRegistry createCore() {
        TemplateRegistryBuilder builder = TemplateRegistry.builder();

//        //create datatype
//        Pattern datatypePattern = PatternService.getPatternFromID("RDFSRANGED");
//        ShaclPattern.ShaclPatternBuilder shaclPatternBuilder = ShaclPattern.builder();
//        shaclPatternBuilder
//                .pattern(datatypePattern)
//                .parameter(CoreArguments.predicate, datatypePattern.getParameter(PrefixNSService.getURIFromAbbrev("rutp:RDFSRANGED-P1")).get())
//                .parameter(CoreArguments.datatype, datatypePattern.getParameter(PrefixNSService.getURIFromAbbrev("rutp:RDFSRANGED-D1")).get())
//                .mainArgument(CoreArguments.datatype);
//
//        builder.shaclPattern(shaclPatternBuilder.build());
        builder.shaclCoreTemplate(createTemplate( CoreArguments.datatype,
                "FILTER NOT EXISTS {\n" +
                "\t\t{ FILTER isLiteral(?value) .} .\n" +
                "\t\tBIND (datatype(?value) AS ?valueDatatype) .\n" +
                "\t\tFILTER (?valueDatatype = $datatype) . }"));

        builder.shaclCoreTemplate(createTemplate( CoreArguments.clazz,
                "FILTER (isLiteral(?value) || \n" +
                        "\t\t!( $class = rdfs:Resource ||\n" +
                        "\t\t\t($class = rdf:List && EXISTS { ?value rdf:first ?any }) ||\n" +
                        "\t\t\tEXISTS { ?value rdf:type/rdfs:subClassOf* $class } ))"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.directType,
                " FILTER NOT EXISTS { ?value a $directType .} "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minLength,
                "FILTER (isBlank(?value) || STRLEN(str(?value)) < $minLength) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxLength,
                "FILTER (isBlank(?value) || STRLEN(str(?value)) > $maxLength) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minExclusive,
                "FILTER (!(?value > $minExclusive)) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minInclusive,
                "FILTER (!(?value >= $minExclusive)) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxExclusive,
                "FILTER (!(?value < $minExclusive)) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxInclusive,
                "FILTER (!(?value <= $minExclusive)) ."));

        return builder.build();
    }

    static ShaclPropertyConstraintTemplate createTemplate(Argument argument, String sparqlSnippet) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(argument.getPredicate().getLocalName())
                .sparqlSnippet(sparqlSnippet)
                .build();
    }
}
