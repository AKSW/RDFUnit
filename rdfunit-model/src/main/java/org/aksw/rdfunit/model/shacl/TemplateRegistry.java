package org.aksw.rdfunit.model.shacl;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PropertyConstraint;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.services.PrefixNSService;

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
    @Getter @NonNull @Singular private final Set<ShaclPattern> shaclPatterns;


    public Set<PropertyConstraint> generatePropertyConstraints(PropertyValuePairSet propertyValuePairSet) {

        return shaclPatterns.stream()
                //get all patterns that can bind to the input
                .filter(p -> p.getArguments().stream().allMatch(a -> a.canBind(propertyValuePairSet)))
                // create bindings for each and map tp ShaclBindingPattern
                .map(p -> {
                    ShaclBindingPattern.ShaclBindingPatternBuilder builder = ShaclBindingPattern.builder();
                    Set<Binding> bindings = p.getArguments().stream()
                            .map(a -> new Binding(p.getParameters().get(a), a.getBindFromValues(propertyValuePairSet).get()))
                            .collect(Collectors.toSet());

                    return builder.pattern(p).bindings(bindings).build();

                }).collect(Collectors.toSet());

}

    public static TemplateRegistry createCore() {
        TemplateRegistryBuilder builder = TemplateRegistry.builder();

        //create datatype
        Pattern datatypePattern = PatternService.getPatternFromID("RDFSRANGED");
        ShaclPattern.ShaclPatternBuilder shaclPatternBuilder = ShaclPattern.builder();
        shaclPatternBuilder
                .pattern(datatypePattern)
                .parameter(CoreArguments.predicate, datatypePattern.getParameter(PrefixNSService.getURIFromAbbrev("rutp:RDFSRANGED-P1")).get())
                .parameter(CoreArguments.datatype, datatypePattern.getParameter(PrefixNSService.getURIFromAbbrev("rutp:RDFSRANGED-D1")).get())
                .mainArgument(CoreArguments.datatype);

        builder.shaclPattern(shaclPatternBuilder.build());

        return builder.build();
    }
}
