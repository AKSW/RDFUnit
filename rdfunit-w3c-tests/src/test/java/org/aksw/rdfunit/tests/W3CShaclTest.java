package org.aksw.rdfunit.tests;

import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import java.nio.file.Paths;

public class W3CShaclTest {

    //TODO
    @Ignore
    @Test
    public void runW3CTests() {
        val rootManifestPath = Paths.get("tests/manifest.ttl");

        val suite = W3CShaclTestSuite.load(rootManifestPath, false);

        //suite.getTestCases().parallelStream().forEach(TestCase::getExecution);
        val failureCount = suite.getTestCases().stream().filter(t -> t.getExecution().isFailure()).count();

        val hist = List.ofAll(suite.getTestCases()).groupBy(W3CShaclTestSuite.TestCase::getEarlOutcome).mapValues(Traversable::size);

        System.out.println(hist);
    }
}
