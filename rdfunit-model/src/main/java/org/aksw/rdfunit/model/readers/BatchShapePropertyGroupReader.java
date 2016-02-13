package org.aksw.rdfunit.model.readers;

import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.PropertyConstraintGroupImpl;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;
import org.aksw.rdfunit.model.shacl.TemplateRegistry;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

public final class BatchShapePropertyGroupReader {

    @NonNull private final TemplateRegistry templateRegistry;

    private BatchShapePropertyGroupReader(TemplateRegistry templateRegistry) {
        this.templateRegistry = templateRegistry;
    }

    public static BatchShapePropertyGroupReader create(@NonNull TemplateRegistry templateRegistry) {
        return new BatchShapePropertyGroupReader(templateRegistry);
    }

    public Set<PropertyConstraintGroup> readShapePropertyGroups(@NonNull Resource shapeResource) {

        ImmutableSet.Builder<PropertyConstraintGroup> propertyGroupsBuilder = ImmutableSet.builder();

        //collect normal property groups
        propertyGroupsBuilder.addAll(
            shapeResource.listProperties(SHACL.property).toList().stream()
                .map(smt -> parsePropertyGroup(shapeResource, smt.getObject().asResource(), false))
                .collect(Collectors.toSet()));

        // collect inverse property groups
        propertyGroupsBuilder.addAll(
                shapeResource.listProperties(SHACL.inverseProperty).toList().stream()
                        .map(smt -> parsePropertyGroup(shapeResource, smt.getObject().asResource(), true))
                        .collect(Collectors.toSet()));

        return propertyGroupsBuilder.build();

    }

    private PropertyConstraintGroup parsePropertyGroup(@NonNull Resource shapeResource, Resource propertyGroup, boolean isInverse) {
        PropertyConstraintGroupImpl.PropertyConstraintGroupImplBuilder builder = PropertyConstraintGroupImpl.builder();

        builder
                .element(propertyGroup)
                .isInverse(isInverse);

        PropertyValuePairSet pvp = PropertyValuePairSet.createFromResource(propertyGroup);
        builder.propertyValuePairSet(pvp);

        Set<RDFNode> values = pvp.getPropertyValues(SHACL.predicate);
        checkState(values.size() == 1, "Predicate occurrence in property group different than 1 in shape: %", shapeResource.getLocalName());
        builder.property(ResourceFactory.createProperty(values.stream().findFirst().get().asResource().getURI()));

        builder.propertyConstraints(templateRegistry.generatePropertyConstraints(pvp));

        return builder.build();
    }

}

