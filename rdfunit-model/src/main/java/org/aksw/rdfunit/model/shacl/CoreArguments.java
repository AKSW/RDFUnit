package org.aksw.rdfunit.model.shacl;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.impl.ShaclCCParameterImpl;
import org.aksw.rdfunit.model.interfaces.ShaclCCParameter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;


public final class CoreArguments {

    public static final ShaclCCParameter predicate = createSimpleArgument(SHACL.predicate, "", ValueKind.IRI);
    public static final ShaclCCParameter severity = createSeverity();

    public static final ShaclCCParameter clazz = createSimpleArgument(SHACL.clazz);
    public static final ShaclCCParameter clazzIn = createSimpleArgument(SHACL.clazzIn);
    public static final ShaclCCParameter datatype = createSimpleArgument(SHACL.datatype, "", ValueKind.IRI);
    public static final ShaclCCParameter datatypeIn = createSimpleArgument(SHACL.datatypeIn);
    public static final ShaclCCParameter directType = createSimpleArgument(SHACL.directType);
    public static final ShaclCCParameter equals = createSimpleArgument(SHACL.equals);
    public static final ShaclCCParameter hasValue = createSimpleArgument(SHACL.hasValue);
    public static final ShaclCCParameter in = createSimpleArgument(SHACL.in);
    public static final ShaclCCParameter lessThan = createSimpleArgument(SHACL.lessThan);
    public static final ShaclCCParameter lessThanOrEquals = createSimpleArgument(SHACL.lessThanOrEquals);
    public static final ShaclCCParameter minCount = createSimpleArgument(SHACL.minCount);
    public static final ShaclCCParameter maxCount = createSimpleArgument(SHACL.maxCount);
    public static final ShaclCCParameter minLength = createSimpleArgument(SHACL.minLength);
    public static final ShaclCCParameter maxLength = createSimpleArgument(SHACL.maxLength);
    public static final ShaclCCParameter minExclusive = createSimpleArgument(SHACL.minExclusive);
    public static final ShaclCCParameter minInclusive = createSimpleArgument(SHACL.minInclusive);
    public static final ShaclCCParameter maxExclusive = createSimpleArgument(SHACL.maxExclusive);
    public static final ShaclCCParameter maxInclusive = createSimpleArgument(SHACL.maxInclusive);
    public static final ShaclCCParameter nodeKind = createSimpleArgument(SHACL.nodeKind);
    public static final ShaclCCParameter notEquals = createSimpleArgument(SHACL.notEquals);
    public static final ShaclCCParameter pattern = createSimpleArgument(SHACL.pattern);
    public static final ShaclCCParameter flags = createSimpleArgument(SHACL.flags, "flags", ValueKind.DATATYPE, true);
    public static final ShaclCCParameter uniqueLang = createSimpleArgument(SHACL.uniqueLang);
    //public static final ShaclCCParameter valueShape
    //public static final ShaclCCParameter qualifiedValueShape
    //public static final ShaclCCParameter qualifiedMinCount
    //public static final ShaclCCParameter qualifiedMaxCount

    private CoreArguments() {}

    private static ShaclCCParameter createSimpleArgument(Property property) {
        return createSimpleArgument(property, property.getLocalName(), ValueKind.IRI);
    }

    private static ShaclCCParameter createSimpleArgument(Property property, String comment, ValueKind valueKind) {
        return createSimpleArgument(property, comment, valueKind, false);
    }

    private static ShaclCCParameter createSimpleArgument(Property property, String comment, ValueKind valueKind, boolean isOptional) {
        return ShaclCCParameterImpl.builder().element(ResourceFactory.createResource())
                .predicate(property)
                .isOptional(isOptional)
                .build();
    }

    private static ShaclCCParameter createSeverity() {
        return ShaclCCParameterImpl.builder().element(ResourceFactory.createResource())
                .predicate(SHACL.severity)
                .defaultValue(ResourceFactory.createResource(RLOGLevel.ERROR.getUri()))
                .isOptional(true)
                .build();
    }
}

