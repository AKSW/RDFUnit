package org.aksw.rdfunit.model.shacl;

import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.impl.ArgumentImpl;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.ResourceFactory;


public class CoreArguments {

    public static final Argument predicate = createPredicate();
    //public static final Argument clazz
    //public static final Argument classIn
    public static final Argument datatype = createDatatype();
    //public static final Argument datatypeIn
    //public static final Argument directType
    //public static final Argument equals
    //public static final Argument hasValue
    //public static final Argument in
    //public static final Argument lessThan
    //public static final Argument lessThanOrEquals
    //public static final Argument minCount
    //public static final Argument maxCount
    //public static final Argument minLength
    //public static final Argument maxLength
    //public static final Argument minExclusive
    //public static final Argument minInclusive
    //public static final Argument maxExclusive
    //public static final Argument maxInclusive
    //public static final Argument nodeKind
    //public static final Argument notEquals
    //public static final Argument pattern
    //public static final Argument uniqueLang
    //public static final Argument valueShape
    //public static final Argument qualifiedValueShape
    //public static final Argument qualifiedMinCount
    //public static final Argument qualifiedMaxCount

    private static Argument createPredicate() {
        return ArgumentImpl.builder().element(ResourceFactory.createResource())
                .predicate(SHACL.predicate)
                .comment("The sh:predicate property")
                .valueKind(ValueKind.IRI)
                .build();
    }

    private static Argument createDatatype() {
        return ArgumentImpl.builder().element(ResourceFactory.createResource())
                .predicate(SHACL.datatype)
                .comment("The sh:datatype property")
                .valueKind(ValueKind.IRI)
                .build();
    }
}

