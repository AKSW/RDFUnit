package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.shacl.PrefixDeclaration;

import java.util.Collection;
import java.util.Set;

public interface GenericTestCase extends Element, Comparable<GenericTestCase>{

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

    default String getTestURI() {return getElement().getURI();}

    default String getAbrTestURI() {return getElement().getLocalName();}

    TestCaseAnnotation getTestCaseAnnotation();

    Collection<PrefixDeclaration> getPrefixDeclarations();

    @Override
    default int compareTo(GenericTestCase o) {
        if (o == null) {
            return -1;
        }

        return this.getTestURI().compareTo(o.getTestURI());
    }
}
