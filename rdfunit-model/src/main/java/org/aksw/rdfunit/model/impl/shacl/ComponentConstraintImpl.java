package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.*;
import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.helper.NodeFormatter;
import org.aksw.rdfunit.model.helper.RdfListUtils;
import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.model.impl.ResultAnnotationImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.*;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.ARQException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder
@Value
public class ComponentConstraintImpl implements ComponentConstraint {
    @Getter @NonNull private final Shape shape;
    @Getter @NonNull private final Literal message;
    @Getter @NonNull private final Component component;
    @NonNull private final ComponentValidator validator;
    @Getter @NonNull @Singular private final ImmutableMap<ComponentParameter, RDFNode> bindings;

    @Override
    public TestCase getTestCase() {

        ManualTestCaseImpl.ManualTestCaseImplBuilder testBuilder = ManualTestCaseImpl.builder();
        String sparql;
        sparql = generateSparqlWhere(validator.getSparqlQuery());


        return testBuilder
                .element(createTestCaseResource())
                .sparqlPrevalence("")
                .sparqlWhere(sparql)
                .prefixDeclarations(validator.getPrefixDeclarations())
                .testCaseAnnotation(generateTestAnnotations())
                .build();
    }
    private String generateSparqlWhere(String sparqlString) {

        if (validator.getType().equals(ComponentValidatorType.ASK_VALIDATOR)) {

            String valuePath;
            if (shape.getPath().isPresent()) {
                valuePath = " ?this " + shape.getPath().get().asSparqlPropertyPath() + " ?value . ";
            } else {
                valuePath = " BIND ($this AS ?value) . ";
            }


            // First check if filter is simple and extract it
            // the we use simple filter not exists
            Pattern p = Pattern.compile("(?i)ASK\\s*\\{\\s*FILTER\\s*\\(([\\s\\S]*)\\)[\\s\\.]*\\}");
            Matcher matcher = p.matcher(sparqlString.trim());

            if ( matcher.find()) {
                String filter = matcher.group(1);

                String adjustedSparql =
                        "ASK { " + valuePath + " \n" +
                            //"FILTER EXISTS {\n" +
                                "BIND ( (" + filter +") AS ?tmp123456)\n" +
                                "FILTER ( !?tmp123456 || !bound(?tmp123456) )\n" +
                            //"}" +
                        "}";
                return replaceBindings(adjustedSparql);
            } else {
                // otherwise we use MINUS

                String sparqlWhere = sparqlString.trim()
                        .replaceFirst("\\{", "")
                        .replaceFirst("ASK", Matcher.quoteReplacement("ASK  {\n " + valuePath + "\n MINUS {\n " + valuePath + " "))
                        + "}";

               return replaceBindings(sparqlWhere);
            }
        } else {
            String  sparqlWhere = sparqlString
                    .substring(sparqlString.indexOf('{'));

            return replaceBindings(sparqlWhere);
        }
    }

    private String replaceBindings(String sparqlSnippet) {
        String bindedSnippet = sparqlSnippet;

        if (shape.isPropertyShape()) {
            bindedSnippet = bindedSnippet.replaceAll(Pattern.quote("$PATH"), Matcher.quoteReplacement(shape.getPath().get().asSparqlPropertyPath()));
        }
        ParameterizedSparqlString query = new ParameterizedSparqlString(bindedSnippet);

        try {
            for (Map.Entry<ComponentParameter, RDFNode> entry : bindings.entrySet()) {
                RDFNode node = entry.getValue();
                if (RdfListUtils.isList(node)) {
                    String value = RdfListUtils.getListItemsOrEmpty(node).stream().map(NodeFormatter::formatNode).collect(Collectors.joining(" , "));
                    query = new ParameterizedSparqlString(
                            query.toString().replaceAll(
                                    Pattern.quote("$"+ entry.getKey().getPredicate().getLocalName()), Matcher.quoteReplacement(value)));
                } else {
                    query.setParam(entry.getKey().getPredicate().getLocalName(), entry.getValue());
                }
            }
            // FIXME add fixed bindings e.g. $shape etc

            return query.toString().trim().replaceFirst("ASK", ""); // remove ASK...
            } catch (ARQException e) {
                // skip this exception
            }
            return sparqlSnippet;
    }

    private String replaceBinding(String sparql, ComponentParameter componentParameter, RDFNode value) {
        return sparql.replaceAll(Pattern.quote("$"+ componentParameter.getPredicate().getLocalName()), Matcher.quoteReplacement(formatRdfValue(value).replace("\\", "\\\\")));
    }

    private Literal generateMessage() {

        if (shape.getMessage().isPresent()) {
            return shape.getMessage().get();
        } else {
            String messageLanguage = message.getLanguage();
            String message = this.message.getLexicalForm();
            //String message = replaceBindings(this.message.getLexicalForm());
            if (messageLanguage == null || messageLanguage.isEmpty()) {
                return ResourceFactory.createStringLiteral(message);
            } else {
                return ResourceFactory.createLangLiteral(message, messageLanguage);
            }
        }
    }

    private String formatRdfValue(RDFNode value) {
        if (RdfListUtils.isList(value)) {
            return RdfListUtils.getListItemsOrEmpty(value).stream().map(NodeFormatter::formatNode).collect(Collectors.joining(" , "));
        } else {
            return NodeFormatter.formatNode(value);
        }
    }

    // hack for now
    private TestCaseAnnotation generateTestAnnotations() {
        return new TestCaseAnnotation(
                createTestCaseResource(),
                TestGenerationType.AutoGenerated,
                null,
                TestAppliesTo.Schema, // TODO check
                SHACL.namespace,      // TODO check
                Collections.emptyList(),
                generateMessage().getLexicalForm(),
                RLOGLevel.ERROR, //FIXME
                createResultAnnotations()
        );
    }

    private List<ResultAnnotation> createResultAnnotations() {
        ImmutableList.Builder<ResultAnnotation> annotations = ImmutableList.builder();
        // add property
        if (shape.getPath().isPresent()) {
            annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.resultPath)
                    .setValue(shape.getPath().get().getPathAsRdf()).build());
        }

        if (shape.getMessage().isPresent()) {
            annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.resultMessage)
                    .setValue(shape.getMessage().get()).build());
        }

        annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.focusNode)
                .setVariableName("this").build());
        annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.resultSeverity)
                .setValue(shape.getSeverity()).build());
        annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.sourceShape)
                    .setValue(shape.getElement()).build());
        annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.sourceConstraintComponent)
                .setValue(component.getElement()).build());


        List<Property> nonValueArgs = Arrays.asList(SHACL.minCount, SHACL.maxCount, SHACL.uniqueLang);
        List<Property> nonValueBind = getBindings().keySet().stream()
                .map(ComponentParameter::getPredicate)
                .filter( p -> !nonValueArgs.contains(p))
                .collect(Collectors.toList());
        if (!nonValueBind.isEmpty()) {
            annotations.add(new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), SHACL.value)
                    .setVariableName("value").build());
        }

        return annotations.build();
    }

    private Resource createTestCaseResource() {
        // FIXME temporary solution until we decide how to build stable unique test uris
        return ResourceFactory.createProperty(JenaUtils.getUniqueIri());
    }
}
