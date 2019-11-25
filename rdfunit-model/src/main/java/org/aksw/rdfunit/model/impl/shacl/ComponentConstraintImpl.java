package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.helper.MessagePrebinding;
import org.aksw.rdfunit.model.helper.QueryPrebinding;
import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.*;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder
@Value
@EqualsAndHashCode (exclude = "sparqlWhere")
public class ComponentConstraintImpl implements ComponentConstraint {
    @Getter @NonNull private final Shape shape;
    @Getter @NonNull private final Component component;
    @NonNull private final ComponentValidator validator;
    @Getter @NonNull @Singular private final ImmutableMap<ComponentParameter, RDFNode> bindings;
    @Getter(lazy = true) @NonNull private final String sparqlWhere = generateSparqlWhere(validator.getSparqlQuery());

    @Override
    public Literal getMessage() {
        Literal messageLiteral =  shape.getMessage().orElseGet(() -> validator.getDefaultMessage().orElse(ResourceFactory.createStringLiteral("Error")));

        String messageLanguage = messageLiteral.getLanguage();
        String message = new MessagePrebinding(messageLiteral.getLexicalForm(), shape).applyBindings(bindings);
        if (messageLanguage == null || messageLanguage.isEmpty()) {
            return ResourceFactory.createStringLiteral(message);
        } else {
            return ResourceFactory.createLangLiteral(message, messageLanguage);
        }
    }

    @Override
    public TestCase getTestCase() {

        ManualTestCaseImpl.ManualTestCaseImplBuilder testBuilder = ManualTestCaseImpl.builder();

        return testBuilder
                .element(createTestCaseResource())
                .sparqlPrevalence("")
                .sparqlWhere(getSparqlWhere())
                .prefixDeclarations(validator.getPrefixDeclarations())
                .testCaseAnnotation(generateTestAnnotations())
                .build();
    }

    private String generateSparqlWhere(String sparqlString) {

        if (validator.getType().equals(ComponentValidatorType.ASK_VALIDATOR)) {

            return generateSparqlWhereFromAskValidator(sparqlString);
        } else {
            String  sparqlWhere = sparqlString
                    .substring(sparqlString.indexOf('{'));

            return replaceBindings(sparqlWhere);
        }
    }

    // match simple ASK / FILTER validators -> we extract the inner filter function
    private static final Pattern patternForExtractingFilterFromSimpleAskValidator = Pattern.compile("(?i)ASK\\s*\\{\\s*FILTER\\s*\\(([\\s\\S]*)\\)[\\s\\.]*\\}");

    private String generateSparqlWhereFromAskValidator(String sparqlString) {
        final String valuePath = shape.getPath()
            .map(ShapePath::asSparqlPropertyPath)
            .map(propertyPath -> " $this " + propertyPath + " $value . ")
            .orElse(" BIND ($this AS $value) . ");


        // First check if filter is simple and extract it
        // the we use simple filter not exists
        Matcher filterMatcher = patternForExtractingFilterFromSimpleAskValidator.matcher(sparqlString.trim());

        if ( filterMatcher.find()) {
            String extractedFilter = filterMatcher.group(1);
            return constructAskQueryBasedOnExtractedFilter(valuePath, extractedFilter);
        } else {
            return constructAskSparqlBasedOnOriginalWithMinus(sparqlString, valuePath);
        }
    }

    private String constructAskSparqlBasedOnOriginalWithMinus(String sparqlString, String valuePath) {
        String sparqlWhere = sparqlString.trim()
                .replaceFirst("\\{", "")
                .replaceFirst("ASK", Matcher.quoteReplacement("ASK  {\n " + valuePath + "\n MINUS {\n " + valuePath + " "))
                + "}";

        return replaceBindings(sparqlWhere);
    }

    private String constructAskQueryBasedOnExtractedFilter(String valuePath, String filter) {
        String adjustedSparql =
                "ASK { " + valuePath + " \n" +
                    //"FILTER EXISTS {\n" +
                        "BIND ( (" + filter +") AS ?tmp123456)\n" +
                        "FILTER ( !?tmp123456 || !bound(?tmp123456) )\n" +
                    //"}" +
                "}";
        return replaceBindings(adjustedSparql);
    }

    private String replaceBindings(String sparqlSnippet) {
        return new QueryPrebinding(sparqlSnippet, shape).applyBindings(bindings);
    }

    // hack for now
    private TestCaseAnnotation generateTestAnnotations() {

        return new TestCaseAnnotation(
                createTestCaseResource(),
                TestGenerationType.AutoGenerated,
                null,
                TestAppliesTo.Schema, // TODO check
                SHACL.namespace,      // TODO check
                ImmutableSet.of(),
                getMessage().getLexicalForm(),
                RLOGLevel.ERROR, //FIXME  shouldnt we use RLOGLevel.resolve here @Dimitris ?
                createResultAnnotations()
        );
    }

    private Set<ResultAnnotation> createResultAnnotations() {
        String prefixes = validator.getPrefixDeclarations().stream()
                .map(p -> "PREFIX " + p.getPrefix() + ": <" + p.getNamespace() + ">")
                .collect(Collectors.joining("\n"));

        String originalSelectClause = validator.getSparqlQuery().substring(0,validator.getSparqlQuery().indexOf('{'));
        String constructedQuery = prefixes + "\n" + originalSelectClause + getSparqlWhere()

                .replaceFirst("(?i)\\s*ASK\\s*\\{", "SELECT \\?this WHERE \\{");

        try {
            Query query = QueryFactory.create(constructedQuery);

            return ResultAnnotationParser.builder()
                    .query(query)
                    .validator(validator)
                    .component(component)
                    .shape(shape)
                    .canBindValueVariable(componentCanBindValueAnnotation())
                    .build()
                    .getResultAnnotations();
        } catch (Exception e) {

            throw new IllegalArgumentException(constructedQuery, e);
        }

    }

    private Resource createTestCaseResource() {
        // FIXME temporary solution until we decide how to build stable unique test uris
        return ResourceFactory.createProperty(JenaUtils.getUniqueIri());
    }

    private static final List<Property> nonValueArgs = ImmutableList.of(SHACL.minCount, SHACL.maxCount, SHACL.uniqueLang);
    private boolean componentCanBindValueAnnotation() {

        return  getBindings().keySet().stream()
                .map(ComponentParameter::getPredicate)
                .noneMatch(nonValueArgs::contains);

    }
}
