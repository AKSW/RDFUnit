package org.aksw.rdfunit.model.interfaces.results;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.SimpleAnnotation;
import org.aksw.rdfunit.model.impl.results.RLOGTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * The type Extended test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:57 PM
 * @version $Id: $Id
 */
public interface ExtendedTestCaseResult extends RLOGTestCaseResult {
    Set<SimpleAnnotation> getResultAnnotations();
}
