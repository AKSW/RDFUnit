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
                // create bindings for each and map to ShaclPropertyConstraintInstance
                .map(p -> {
                    ShaclPropertyConstraintInstance.ShaclPropertyConstraintInstanceBuilder builder = ShaclPropertyConstraintInstance.builder();

                    p.getArguments().stream().forEach( arg -> builder.binding(arg, arg.getBindFromValues(propertyValuePairSet).get()));

                    return builder.template(p).build();

                }).collect(Collectors.toSet());

    }

    public static TemplateRegistry createCore() {
        TemplateRegistryBuilder builder = TemplateRegistry.builder();

        builder.shaclCoreTemplate(createTemplate( CoreArguments.datatype,
                "sh:datatype of $predicate should be '$datatype'",
                " FILTER NOT EXISTS {\n" +
                "\t\t{ FILTER isLiteral(?value) .} .\n" +
                "\t\tBIND (datatype(?value) AS ?valueDatatype) .\n" +
                "\t\tFILTER (?valueDatatype = $datatype) . } "));

        //sh:datatypeIn

        builder.shaclCoreTemplate(createTemplate( CoreArguments.clazz,
                "sh:class of $predicate should be '$class'",
                " FILTER (isLiteral(?value) || \n" +
                        "\t\t!( $class = rdfs:Resource ||\n" +
                        "\t\t\t($class = rdf:List && EXISTS { ?value rdf:first ?any }) ||\n" +
                        "\t\t\tEXISTS { ?value rdf:type/rdfs:subClassOf* $class } )) "));
        //sh:classIn

        builder.shaclCoreTemplate( createTemplate( CoreArguments.directType,
                "sh:directType of $predicate should be '$directType'",
                " FILTER NOT EXISTS { ?value a $directType .} "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.equals,
                "$predicate should be equal to '$equals'",
                " FILTER NOT EXISTS { ?this $equals ?value . } "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.hasValue,
                "$predicate have value: $hasValue",
                " FILTER NOT EXISTS { ?this $predicate $hasValue . } "));

        //TODO sh:in

        builder.shaclCoreTemplate( createTemplate( CoreArguments.lessThan,
                "$predicate should be less than '$lessThan'",
                " ?this $lessThan ?value2 .\n" +
                "\tFILTER (!(?value < ?value2)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.lessThanOrEquals,
                "$predicate should be less than or equals to '$lessThanOrEquals'",
                " ?this $lessThan ?value2 .\n" +
                        "\tFILTER (!(?value <= ?value2)) . "));


        //TODO sh:minCount,

        //TODO sh:maxCount

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minLength,
                "sh:minLength of $predicate should be '$minLength'",
                " FILTER (isBlank(?value) || STRLEN(str(?value)) < $minLength) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxLength,
                "sh:maxLength of $predicate should be '$maxLength'",
                " FILTER (isBlank(?value) || STRLEN(str(?value)) > $maxLength) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minExclusive,
                "sh:minExclusive of $predicate should be '$minExclusive'",
                " FILTER (!(?value > $minExclusive)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minInclusive,
                "sh:minInclusive of $predicate should be '$minInclusive'",
                " FILTER (!(?value >= $minInclusive)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxExclusive,
                "sh:maxExclusive of $predicate should be '$maxExclusive'",
                " FILTER (!(?value < $maxExclusive)) . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxInclusive,
                "sh:maxInclusive of $predicate should be '$maxInclusive'",
                " FILTER (!(?value <= $maxInclusive)) . "));


        builder.shaclCoreTemplate( createTemplate( CoreArguments.nodeKind,
                "sh:nodeKind of $predicate should be '$nodeKind'",
                "\tFILTER NOT EXISTS {\n" +
                "\t\tFILTER ((isIRI(?value) && $nodeKind = sh:IRI) ||\n" +
                "\t\t\t(isLiteral(?value) && $nodeKind = sh:Literal) ||\n" +
                "\t\t\t(isBlank(?value) && $nodeKind = sh:BlankNode)) . } "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.notEquals,
                "$predicate should no be equal to '$notEquals'",
                " ?this $notEquals ?value . "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.pattern, CoreArguments.flags,
                "Value $predicate should conform to pattern: '$pattern'",
                " BIND ('$flags' AS ?myFlags) . FILTER (isBlank(?value) || IF(?myFlags != '', !regex(str(?value), '$pattern', '$flags'), !regex(str(?value), '$pattern'))) ."));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.uniqueLang,
                "$predicate should have one value per language",
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

    static ShaclPropertyConstraintTemplate createTemplate(Argument argument, String message, String sparqlSnippet) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(message)
                .sparqlSnippet(sparqlSnippet)
                .build();
    }

    static ShaclPropertyConstraintTemplate createTemplate(Argument argument1, Argument argument2, String message, String sparqlSnippet) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument1)
                .argument(argument2)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(message)
                .sparqlSnippet(sparqlSnippet)
                .build();
    }
}
