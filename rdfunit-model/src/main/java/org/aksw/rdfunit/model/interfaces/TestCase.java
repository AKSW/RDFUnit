package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.util.Collection;
import java.util.Set;

/**
 * <p>TestCase interface.</p>
 *
 * @author Dimitris Kontokostas
 * @since 9/23/13 6:31 AM
 * @version $Id: $Id
 */
public interface TestCase extends Element, Comparable<TestCase>{

    String getSparqlWhere();

    String getSparqlPrevalence();

    default String getResultMessage()  {
        return getTestCaseAnnotation().getDescription();
    }

    default RLOGLevel getLogLevel()  {
        return getTestCaseAnnotation().getTestCaseLogLevel();
    }

    default Set<ResultAnnotation> getResultAnnotations()  {
        return getTestCaseAnnotation().getResultAnnotations();
    }

    default Set<ResultAnnotation> getVariableAnnotations() {
        return getTestCaseAnnotation().getVariableAnnotations();
    }

    default Query getSparqlPrevalenceQuery() {
        if (getSparqlPrevalence().trim().isEmpty()) {
            return null;
        }
        return QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getSparqlPrevalence());}

    default String getTestURI() {return getElement().getURI();}

    default String getAbrTestURI() {return getElement().getLocalName();}

    TestCaseAnnotation getTestCaseAnnotation();

    Collection<PrefixDeclaration> getPrefixDeclarations();

    @Override
    default int compareTo(TestCase o) {
        if (o == null) {
            return -1;
        }

        return this.getTestURI().compareTo(o.getTestURI());
    }

}
