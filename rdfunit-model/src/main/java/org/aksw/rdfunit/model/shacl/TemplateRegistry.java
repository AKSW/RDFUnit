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
                // check if any filter applies
                .filter(p -> p.canBind(propertyValuePairSet))
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
                "\t\tFILTER (?valueDatatype = $datatype) . } }"));

        builder.shaclCoreTemplate(createTemplate( CoreArguments.datatypeIn,
                "sh:datatype of $predicate should be ($datatypeIn)",
                "FILTER (!isLiteral(?value) || NOT EXISTS {\n" +
                        "\t\t\tBIND (datatype(?value) AS ?valueDatatype) .\n" +
                        "\t\t\tVALUES ?valueDatatype { $datatypeIn } .\n" +
                        "\t\t})\n" +
                        "\t}\n" ));

        builder.shaclCoreTemplate(createTemplate( CoreArguments.clazz,
                "sh:class of $predicate should be '$class'",
                " FILTER (isLiteral(?value) || \n" +
                        "\t\t!( $class = rdfs:Resource ||\n" +
                        "\t\t\t($class = rdf:List && EXISTS { ?value rdf:first ?any }) ||\n" +
                        "\t\t\tEXISTS { ?value rdf:type/rdfs:subClassOf* $class } )) }"));

        builder.shaclCoreTemplate(createTemplate( CoreArguments.clazzIn,
                "sh:classIn of $predicate should be in ($classIn)",
                "FILTER (isLiteral(?value) || NOT EXISTS {\n" +
                        "\t\t\tFILTER (?class = rdfs:Resource ||\n" +
                        "\t\t\t\t(?class = rdf:List && EXISTS { ?value rdf:first ?any }) ||\n" +
                        "\t\t\t\tEXISTS { ?value rdf:type/rdfs:subClassOf* ?c . VALUES ?c { $classIn } })\n" +
                        "\t\t})\n" +
                        "}"));


        builder.shaclCoreTemplate( createTemplate( CoreArguments.directType,
                "sh:directType of $predicate should be '$directType'",
                " FILTER NOT EXISTS { ?value a $directType .} }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.equals,
                "$predicate should be equal to '$equals'",
                " FILTER NOT EXISTS { ?this $equals ?value . } }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.hasValue,
                "$predicate have value: $hasValue",
                " FILTER NOT EXISTS { ?this $predicate $hasValue . } }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.in,
                "$predicate have value: $in",
        "FILTER NOT EXISTS { VALUES ?value { $in }  } } " ));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.lessThan,
                "$predicate should be less than '$lessThan'",
                " ?this $lessThan ?value2 .\n" +
                "\tFILTER (!(?value < ?value2)) .} "));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.lessThanOrEquals,
                "$predicate should be less than or equals to '$lessThanOrEquals'",
                " ?this $lessThan ?value2 .\n" +
                        "\tFILTER (!(?value <= ?value2)) . }"));


        builder.shaclCoreTemplate( createTemplateWithFilter( CoreArguments.minCount,
                "Minimum cardinality for $predicate is '$minCount'",
                " } GROUP BY ?this\n" +
                " HAVING ( ( count(?value)  < $minCount ) && ( count(?value)  != 0 ) ) ",
                " ASK { FILTER ($minCount > 1)}"));
        builder.shaclCoreTemplate( createTemplateWithFilterNF( CoreArguments.minCount,
                "Minimum cardinality for $predicate is '$minCount'1",
                " FILTER NOT EXISTS { ?this $predicate ?value }} ",
                " FILTER NOT EXISTS { ?this $predicate ?value }} ", // is inverse property like this?
                " ASK { FILTER ($minCount > 0)}"));

        builder.shaclCoreTemplate( createTemplateWithFilter( CoreArguments.maxCount,
                "Maximum cardinality for $predicate is '$maxCount'",
                " } GROUP BY ?this\n" +
                " HAVING ( ( count(?value)  > $maxCount ) && ( count(?value)  != 0 ) ) ",
                " ASK { FILTER ($maxCount > 0)}"));
        builder.shaclCoreTemplate( createTemplateWithFilterNF( CoreArguments.maxCount,
                "Maximum cardinality for $predicate is '$maxCount'0",
                " FILTER EXISTS { ?this $predicate ?value }} ",
                " FILTER EXISTS { ?this $predicate ?value }} ", // is inverse property like this?
                " ASK { FILTER ($maxCount = 0)}"));


        builder.shaclCoreTemplate( createTemplate( CoreArguments.minLength,
                "sh:minLength of $predicate should be '$minLength'",
                " FILTER (isBlank(?value) || STRLEN(str(?value)) < $minLength) . }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxLength,
                "sh:maxLength of $predicate should be '$maxLength'",
                " FILTER (isBlank(?value) || STRLEN(str(?value)) > $maxLength) . }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minExclusive,
                "sh:minExclusive of $predicate should be '$minExclusive'",
                " FILTER (!(?value > $minExclusive)) . }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.minInclusive,
                "sh:minInclusive of $predicate should be '$minInclusive'",
                " FILTER (!(?value >= $minInclusive)) . }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxExclusive,
                "sh:maxExclusive of $predicate should be '$maxExclusive'",
                " FILTER (!(?value < $maxExclusive)) . }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.maxInclusive,
                "sh:maxInclusive of $predicate should be '$maxInclusive'",
                " FILTER (!(?value <= $maxInclusive)) . }"));


        builder.shaclCoreTemplate( createTemplateWithFilter( CoreArguments.nodeKind,
                "sh:nodeKind of $predicate should be '$nodeKind'",
                " FILTER (!isIRI(?value)) .  } ",
                " ASK { FILTER ($nodeKind = <http://www.w3.org/ns/shacl#IRI>)}"));
        builder.shaclCoreTemplate( createTemplateWithFilter( CoreArguments.nodeKind,
                "sh:nodeKind of $predicate should be '$nodeKind'",
                " FILTER (!isLiteral(?value)) .  } ",
                " ASK { FILTER ($nodeKind = <http://www.w3.org/ns/shacl#Literal>)}"));
        builder.shaclCoreTemplate( createTemplateWithFilter( CoreArguments.nodeKind,
                "sh:nodeKind of $predicate should be '$nodeKind'",
                " FILTER (!isBlank(?value)) .  } ",
                " ASK { FILTER ($nodeKind = <http://www.w3.org/ns/shacl#BlankNode>)}"));


        builder.shaclCoreTemplate( createTemplate( CoreArguments.notEquals,
                "$predicate should no be equal to '$notEquals'",
                " ?this $notEquals ?value . }"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.pattern, CoreArguments.flags,
                "Value $predicate should conform to pattern: '$pattern'",
                " BIND ('$flags' AS ?myFlags) . FILTER (isBlank(?value) || IF(?myFlags != '', !regex(str(?value), '$pattern', '$flags'), !regex(str(?value), '$pattern'))) .}"));

        builder.shaclCoreTemplate( createTemplate( CoreArguments.uniqueLang,
                "$predicate should have one value per language",
                " {FILTER ($uniqueLang) . }\n" +
                "\tBIND (lang(?value) AS ?lang) .\n" +
                "\tFILTER (bound(?lang) && ?lang != \"\") . \n" +
                "\tFILTER EXISTS {\n" +
                "\t\t?this $predicate ?otherValue .\n" +
                "\t\tFILTER (?otherValue != ?value && ?lang = lang(?otherValue)) . } }" ));

        //TODO sh:valueShape

        //TODO sh:qualifiedValueShape, sh:qualifiedMinCount, sh:qualifiedMaxCount

        return builder.build();
    }

    private static ShaclPropertyConstraintTemplate createTemplate(Argument argument, String message, String sparqlSnippet) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(message)
                .sparqlPropSnippet(sparqlSnippet)
                .sparqlInvPSnippet(sparqlSnippet)
                .includePropertyFilter(true)
                .build();
    }

    private static ShaclPropertyConstraintTemplate createTemplateWithFilter(Argument argument, String message, String sparqlSnippet, String filter) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(message)
                .sparqlPropSnippet(sparqlSnippet)
                .sparqlInvPSnippet(sparqlSnippet)
                .argumentFilter(argument, filter)
                .includePropertyFilter(true)
                .build();
    }

    private static ShaclPropertyConstraintTemplate createTemplateWithFilterNF(Argument argument, String message, String sparqlPropSnippet, String sparqlInvPSnippet, String filter) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(message)
                .sparqlPropSnippet(sparqlPropSnippet)
                .sparqlInvPSnippet(sparqlInvPSnippet)
                .argumentFilter(argument, filter)
                .includePropertyFilter(false)
                .build();
    }

    private static ShaclPropertyConstraintTemplate createTemplate(Argument argument1, Argument argument2, String message, String sparqlSnippet) {
        return ShaclPropertyConstraintTemplate.builder()
                .argument(argument1)
                .argument(argument2)
                .argument(CoreArguments.predicate)
                .argument(CoreArguments.severity)
                .message(message)
                .sparqlPropSnippet(sparqlSnippet)
                .sparqlInvPSnippet(sparqlSnippet)
                .includePropertyFilter(true)
                .build();
    }
}
