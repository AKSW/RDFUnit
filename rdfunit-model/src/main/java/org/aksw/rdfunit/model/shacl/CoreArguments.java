package org.aksw.rdfunit.model.shacl;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.impl.shacl.ComponentParameterImpl;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;


public final class CoreArguments {

    public static final ComponentParameter path = createSimpleArgument(SHACL.path, "", ValueKind.IRI);
    public static final ComponentParameter severity = createSeverity();

    public static final ComponentParameter clazz = createSimpleArgument(SHACL.clazz);
    public static final ComponentParameter clazzIn = createSimpleArgument(SHACL.clazzIn);
    public static final ComponentParameter datatype = createSimpleArgument(SHACL.datatype, "", ValueKind.IRI);
    public static final ComponentParameter datatypeIn = createSimpleArgument(SHACL.datatypeIn);
    public static final ComponentParameter directType = createSimpleArgument(SHACL.directType);
    public static final ComponentParameter equals = createSimpleArgument(SHACL.equals);
    public static final ComponentParameter hasValue = createSimpleArgument(SHACL.hasValue);
    public static final ComponentParameter in = createSimpleArgument(SHACL.in);
    public static final ComponentParameter lessThan = createSimpleArgument(SHACL.lessThan);
    public static final ComponentParameter lessThanOrEquals = createSimpleArgument(SHACL.lessThanOrEquals);
    public static final ComponentParameter minCount = createSimpleArgument(SHACL.minCount);
    public static final ComponentParameter maxCount = createSimpleArgument(SHACL.maxCount);
    public static final ComponentParameter minLength = createSimpleArgument(SHACL.minLength);
    public static final ComponentParameter maxLength = createSimpleArgument(SHACL.maxLength);
    public static final ComponentParameter minExclusive = createSimpleArgument(SHACL.minExclusive);
    public static final ComponentParameter minInclusive = createSimpleArgument(SHACL.minInclusive);
    public static final ComponentParameter maxExclusive = createSimpleArgument(SHACL.maxExclusive);
    public static final ComponentParameter maxInclusive = createSimpleArgument(SHACL.maxInclusive);
    public static final ComponentParameter nodeKind = createSimpleArgument(SHACL.nodeKind);
    public static final ComponentParameter notEquals = createSimpleArgument(SHACL.notEquals);
    public static final ComponentParameter pattern = createSimpleArgument(SHACL.pattern);
    public static final ComponentParameter flags = createSimpleArgument(SHACL.flags, "flags", ValueKind.DATATYPE, true);
    public static final ComponentParameter uniqueLang = createSimpleArgument(SHACL.uniqueLang);
    //public static final ComponentParameter node
    //public static final ComponentParameter qualifiedValueShape
    //public static final ComponentParameter qualifiedMinCount
    //public static final ComponentParameter qualifiedMaxCount

    private CoreArguments() {}

    private static ComponentParameter createSimpleArgument(Property property) {
        return createSimpleArgument(property, property.getLocalName(), ValueKind.IRI);
    }

    private static ComponentParameter createSimpleArgument(Property property, String comment, ValueKind valueKind) {
        return createSimpleArgument(property, comment, valueKind, false);
    }

    private static ComponentParameter createSimpleArgument(Property property, String comment, ValueKind valueKind, boolean isOptional) {
        return ComponentParameterImpl.builder().element(ResourceFactory.createResource())
                .predicate(property)
                .isOptional(isOptional)
                .build();
    }

    private static ComponentParameter createSeverity() {
        return ComponentParameterImpl.builder().element(ResourceFactory.createResource())
                .predicate(SHACL.severity)
                .defaultValue(ResourceFactory.createResource(RLOGLevel.ERROR.getUri()))
                .isOptional(true)
                .build();
    }
}

