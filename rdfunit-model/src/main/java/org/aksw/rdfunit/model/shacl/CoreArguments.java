package org.aksw.rdfunit.model.shacl;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.impl.ArgumentImpl;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;


public class CoreArguments {

    public static final Argument predicate = createSimpleArgument(SHACL.predicate, "", ValueKind.IRI);
    public static final Argument severity = createSeverity();

    public static final Argument clazz = createSimpleArgument(SHACL.clazz);
    public static final Argument clazzIn = createSimpleArgument(SHACL.clazzIn);
    public static final Argument datatype = createSimpleArgument(SHACL.datatype, "", ValueKind.IRI);
    public static final Argument datatypeIn = createSimpleArgument(SHACL.datatypeIn);
    public static final Argument directType = createSimpleArgument(SHACL.directType);
    public static final Argument equals = createSimpleArgument(SHACL.equals);
    public static final Argument hasValue = createSimpleArgument(SHACL.hasValue);
    public static final Argument in = createSimpleArgument(SHACL.in);
    public static final Argument lessThan = createSimpleArgument(SHACL.lessThan);
    public static final Argument lessThanOrEquals = createSimpleArgument(SHACL.lessThanOrEquals);
    public static final Argument minCount = createSimpleArgument(SHACL.minCount);
    public static final Argument maxCount = createSimpleArgument(SHACL.maxCount);
    public static final Argument minLength = createSimpleArgument(SHACL.minLength);
    public static final Argument maxLength = createSimpleArgument(SHACL.maxLength);
    public static final Argument minExclusive = createSimpleArgument(SHACL.minExclusive);
    public static final Argument minInclusive = createSimpleArgument(SHACL.minInclusive);
    public static final Argument maxExclusive = createSimpleArgument(SHACL.maxExclusive);
    public static final Argument maxInclusive = createSimpleArgument(SHACL.maxInclusive);
    public static final Argument nodeKind = createSimpleArgument(SHACL.nodeKind);
    public static final Argument notEquals = createSimpleArgument(SHACL.notEquals);
    public static final Argument pattern = createSimpleArgument(SHACL.pattern);
    public static final Argument flags = createSimpleArgument(SHACL.flags, "flags", ValueKind.DATATYPE, true);
    public static final Argument uniqueLang = createSimpleArgument(SHACL.uniqueLang);
    //public static final Argument valueShape
    //public static final Argument qualifiedValueShape
    //public static final Argument qualifiedMinCount
    //public static final Argument qualifiedMaxCount

    private static Argument createSimpleArgument(Property property) {
        return createSimpleArgument(property, property.getLocalName(), ValueKind.IRI);
    }

    private static Argument createSimpleArgument(Property property, String comment, ValueKind valueKind) {
        return createSimpleArgument(property, comment, valueKind, false);
    }

    private static Argument createSimpleArgument(Property property, String comment, ValueKind valueKind, boolean isOptional) {
        return ArgumentImpl.builder().element(ResourceFactory.createResource())
                .predicate(property)
                .comment(comment)
                .valueKind(valueKind)
                .isOptional(isOptional)
                .build();
    }

    private static Argument createSeverity() {
        return ArgumentImpl.builder().element(ResourceFactory.createResource())
                .predicate(SHACL.severity)
                .comment("")
                .defaultValue(ResourceFactory.createResource(RLOGLevel.ERROR.getUri()))
                .isOptional(true)
                .valueKind(ValueKind.IRI)
                .build();
    }
}

