package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.apache.jena.rdf.model.Resource;

/**
 * ManualTestCase Implementation
 *
 * @author Dimitris Kontokostas
 * @since 1/3/14 3:57 PM
 * @version $Id: $Id
 */
@Builder
@EqualsAndHashCode(exclude = "element")
@ToString
public class ManualTestCaseImpl implements TestCase {
    @Getter @NonNull private final Resource element;
    @Getter @NonNull private final TestCaseAnnotation testCaseAnnotation;
    @Getter @NonNull @Singular private final ImmutableSet<PrefixDeclaration> prefixDeclarations;

    @Getter @NonNull private final String sparqlWhere;
    @Getter @NonNull private final String sparqlPrevalence;

}
