package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.NonNull;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.model.impl.ScopedTestCase;
import org.aksw.rdfunit.model.interfaces.Shape;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.readers.BatchShapeReader;
import org.apache.jena.rdf.model.Model;

import java.util.Set;

/**
 * Represents a SHACL Model
 *
 * @author Dimitris Kontokostas
 * @since 29/1/2016 9:28 πμ
 */

public class ShaclModel {
    @NonNull private final ImmutableSet<Shape> shapes;

    @Getter @NonNull private final TemplateRegistry templateRegistry;

    // TODO do not use Model for instantiation, change later
    public ShaclModel(Model shaclGraph) throws RdfReaderException {

        // read templates from Model, for now only use fixed core
        this.templateRegistry = TemplateRegistry.createCore();
        this.shapes = ImmutableSet.copyOf(BatchShapeReader.create(templateRegistry).getShapesFromModel(shaclGraph));

    }

    public Set<Shape> getShapes() { return shapes;}

    public Set<TestCase> generateTestCases() {
        ImmutableSet.Builder<TestCase> builder = ImmutableSet.builder();

        getShapes().forEach( shape -> // for every shape
                shape.getScopes().forEach( scope ->  // for every scope (skip if none)
                                shape.getPropertyConstraintGroups().forEach( ppg ->
                                    ppg.getPropertyConstraints().forEach( ppc -> {
                                        String filter = ppg.getPropertyFilter();
                                        if (!ppc.usesValidatorFunction()) {filter = "";}
                                        builder.add(
                                                ScopedTestCase.builder()
                                                        .scope(scope)
                                                        .filterSpqrql(filter)
                                                        .testCase(ppc.getTestCase(ppg.isInverse()))
                                                        .build());
                                    } ))));

        return builder.build();
    }
}
