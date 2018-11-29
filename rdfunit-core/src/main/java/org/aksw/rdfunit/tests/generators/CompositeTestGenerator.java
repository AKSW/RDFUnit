package org.aksw.rdfunit.tests.generators;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.TestSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Compopsite test generator that executes all test generators
 * i.e. used in executing both manual and tag tests
 *
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */
public class CompositeTestGenerator implements RdfUnitTestGenerator{

    private final ImmutableList<RdfUnitTestGenerator> generators;

    public CompositeTestGenerator(List<RdfUnitTestGenerator> generators) {
        this.generators = ImmutableList.copyOf(generators);
    }

    @Override
    public Collection<TestCase> generate(SchemaSource source) {
        ArrayList<TestCase> tcs = Lists.newArrayList();
        for(RdfUnitTestGenerator g : generators){
            tcs.addAll(g.generate(source));
        }
        return tcs;
    }


    @Override
    public Collection<TestCase> generate(TestSource source) {
        ArrayList<TestCase> tcs = Lists.newArrayList();
        for(RdfUnitTestGenerator g : generators){
            tcs.addAll(g.generate(source));
        }
        return tcs;
    }
}
