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

        builder.shaclCoreTemplate(createTemplate( CoreArguments.datatype,
                " FILTER NOT EXISTS {\n" +
                "\t\t{ FILTER isLiteral(?value) .} .\n" +
                "\t\tBIND (datatype(?value) AS ?valueDatatype) .\n" +
                "\t\tFILTER (?valueDatatype = $datatype) . } "));

        //sh:datatypeIn

        builder.shaclCoreTemplate(createTemplate( CoreArguments.clazz,
                " FILTER (isLiteral(?value) || \n" +
                        "\t\t!( $class = rdfs:Resource ||\n" +
                        "\t\t\t($class = rdf:List && EXISTS { ?value rdf:first ?any }) ||\n" +
                        "\t\t\tEXISTS { ?value rdf:type/rdfs:subClassOf* $class } )) "));
        //sh:classIn

        builder.shaclCoreTemplate( createTemplate( CoreArguments.directType,
                " FILTER NOT EXISTS { ?value a $directType .} "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.equals,
                "FILTER NOT EXISTS { ?this $equals ?value . } "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.hasValue,
                " FILTER NOT EXISTS { ?this $predicate $hasValue . } "));

        //TODO sh:in

        builder.shaclCoreTemplate( createTemplate( CoreArguments.lessThan,
                " ?this $lessThan ?value2 .\n" +
                "\tFILTER (!(?value < ?value2)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.lessThanOrEquals,
                " ?this $lessThan ?value2 .\n" +
                        "\tFILTER (!(?value <= ?value2)) . "));


        //TODO sh:minCount,

        //TODO sh:maxCount

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minLength,
                " FILTER (isBlank(?value) || STRLEN(str(?value)) < $minLength) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxLength,
                " FILTER (isBlank(?value) || STRLEN(str(?value)) > $maxLength) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minExclusive,
                " FILTER (!(?value > $minExclusive)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minInclusive,
                " FILTER (!(?value >= $minInclusive)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxExclusive,
                " FILTER (!(?value < $maxExclusive)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxInclusive,
                " FILTER (!(?value <= $maxInclusive)) . "));


        builder.shaclCoreTemplate( createTemplate( CoreArguments.nodeKind,
                "\tFILTER NOT EXISTS {\n" +
                "\t\tFILTER ((isIRI(?value) && $nodeKind = sh:IRI) ||\n" +
                "\t\t\t(isLiteral(?value) && $nodeKind = sh:Literal) ||\n" +
                "\t\t\t(isBlank(?value) && $nodeKind = sh:BlankNode)) . } "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.notEquals,
                " ?this $notEquals ?value . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.pattern, CoreArguments.flags,
                "FILTER (isBlank(?value) || IF(bound($flags), !regex(str(?value), $pattern, $flags), !regex(str(?value), $pattern))) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.uniqueLang,
                " {FILTER ($uniqueLang) . }\n" +
                "\tBIND (lang(?value) AS ?lang) .\n" +
                "\tFILTER (bound(?lang) && ?lang != \"\") . \n" +
                "\tFILTER EXISTS {\n" +
                "\t\t?this $predicate ?otherValue .\n" +
                "\t\tFILTER (?otherValue != ?value && ?lang = lang(?otherValue)) . } " ));

        //TODO sh:valueShape

        //TODO sh:qualifiedValueShape, sh:qualifiedMinCount, sh:qualifiedMaxCount

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

    static ShaclPropertyConstraintTemplate createTemplate(Argument argument1, Argument argument2, String sparqlSnippet) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument1)
                .argument(argument2)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(argument1.getPredicate().getLocalName())
                .sparqlSnippet(sparqlSnippet)
                .build();
    }
}
