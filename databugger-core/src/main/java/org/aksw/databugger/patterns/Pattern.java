package org.aksw.databugger.patterns;

import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Class that holds a sparqlWherePattern definition
 * Created: 9/16/13 1:14 PM
 */
public class Pattern {
    private final String id;
    private final String description;
    private final String sparqlWherePattern;
    private final String sparqlPatternPrevalence;
    private final List<PatternParameter> parameters;

    public Pattern(String id, String description, String sparqlWherePattern, String sparqlPatternPrevalence, List<PatternParameter> parameters) {
        this.id = id;
        this.description = description;
        this.sparqlWherePattern = sparqlWherePattern;
        this.sparqlPatternPrevalence = sparqlPatternPrevalence;
        this.parameters = parameters;
    }

    public boolean isValid() {
        if (getParameters() == null || getParameters().size() == 0)
            return false;
        //check if defined parameters exist is sparql
        for (PatternParameter p : getParameters()) {
            if (getSparqlWherePattern().indexOf("%%" + p.getId() + "%%") == -1)
                return false;
        }
        // TODO search if we need more parameters
        return true;
    }

    // TODO assumes ordered bindings / parameters might not do like this in the end
    public String instantiateSparqlPattern(List<String> bindings) throws Exception {
        if (bindings.size() != getParameters().size())
            throw new Exception("Bindings different in number than parameters");
        return instantiateBindings(bindings, getSparqlWherePattern());
    }

    public String instantiateSparqlPatternPrevalence(List<String> bindings) throws Exception {
        if (bindings.size() != getParameters().size())
            throw new Exception("Bindings different in number than parameters");
        return instantiateBindings(bindings, getSparqlPatternPrevalence());
    }

    private String instantiateBindings(List<String> bindings, String query) {
        String sparql = query;
        for (int i = 0; i < bindings.size(); i++) {
            sparql = sparql.replace("%%" + getParameters().get(i).getId() + "%%", bindings.get(i));
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

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSparqlWherePattern() {
        return sparqlWherePattern;
    }

    public String getSparqlPatternPrevalence() {
        return sparqlPatternPrevalence;
    }

    public List<PatternParameter> getParameters() {
        return parameters;
    }
}
