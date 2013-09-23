package org.aksw.databugger.patterns;

import org.aksw.databugger.tests.TestAnnotation;
import org.aksw.databugger.tests.UnitTest;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Class that holds a pattern definition
 * Created: 9/16/13 1:14 PM
 */
public class Pattern {
    public final String id;
    public final String description;
    public final String pattern;
    public final String patternPrevalence;
    public final String selectVariable;
    public final List<PatternParameter> parameters;

    public Pattern(String id, String description, String pattern, String patternPrevalence, String selectVariable, List<PatternParameter> parameters) {
        this.id = id;
        this.description = description;
        this.pattern = pattern;
        this.patternPrevalence = patternPrevalence;
        this.selectVariable = selectVariable;
        this.parameters = parameters;
        // TODO Check validadateArguments otherwise throw exception
    }

    public UnitTest instantiate(List<String> bindings, String sourceURI, TestAnnotation annotation) throws Exception {

        if (bindings.size() != parameters.size()) throw new Exception("Bindings different in number than parameters");

        String sparql = pattern;
        String sparqlPrevalence = patternPrevalence;
        for (int i = 0; i < bindings.size(); i++) {
            sparql = sparql.replace("%%" + parameters.get(i).id + "%%", bindings.get(i));
            sparqlPrevalence = sparqlPrevalence.replace("%%" + parameters.get(i).id + "%%", bindings.get(i));

        }

        return new UnitTest(sourceURI, id, annotation, sparql, sparqlPrevalence);

    }

    /*
    * Checks if all given arguments exist in the patters and the opposite
    * */
    private boolean validateArguments() {
        //TODO implement this method
        return true;
    }
}
