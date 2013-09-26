package org.aksw.databugger.patterns;

import org.aksw.databugger.tests.TestAnnotation;
import org.aksw.databugger.tests.UnitTest;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Class that holds a sparqlPattern definition
 * Created: 9/16/13 1:14 PM
 */
public class Pattern {
    public final String id;
    public final String description;
    public final String sparqlPattern;
    public final String sparqlPatternPrevalence;
    public final String selectVariable;
    public final List<PatternParameter> parameters;

    public Pattern(String id, String description, String sparqlPattern, String sparqlPatternPrevalence, String selectVariable, List<PatternParameter> parameters) {
        this.id = id;
        this.description = description;
        this.sparqlPattern = sparqlPattern;
        this.sparqlPatternPrevalence = sparqlPatternPrevalence;
        this.selectVariable = selectVariable;
        this.parameters = parameters;
    }

    public boolean isValid(){
        if (parameters == null || parameters.size() == 0)
            return false;
        //check if defined parameters exist is sparql
        for (PatternParameter p: parameters) {
            if ( sparqlPattern.indexOf("%%" + p.id + "%%") == -1 )
                return false;
        }
        // TODO search if we need more parameters
        return true;
    }


    // TODO assumes ordered bindings / parameters might not do like this in the end
    public String instantiateSparqlPattern(List<String> bindings) throws Exception {
        if (bindings.size() != parameters.size()) throw new Exception("Bindings different in number than parameters");
        return instantiateBindings(bindings,sparqlPattern);
    }

    public String instantiateSparqlPatternPrevalence(List<String> bindings) throws Exception {
        if (bindings.size() != parameters.size()) throw new Exception("Bindings different in number than parameters");
        return instantiateBindings(bindings,sparqlPatternPrevalence);
    }


    private String instantiateBindings(List<String> bindings, String query) {
        String sparql = query;
        for (int i = 0; i < bindings.size(); i++) {
            sparql = sparql.replace("%%" + parameters.get(i).id + "%%", bindings.get(i));
        }
        return sparql;
    }

    /*
    * Checks if all given arguments exist in the patters and the opposite
    * */
    private boolean validateArguments() {
        //TODO implement this method
        return true;
    }
}
