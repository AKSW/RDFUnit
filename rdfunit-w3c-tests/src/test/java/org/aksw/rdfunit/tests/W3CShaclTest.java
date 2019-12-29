package org.aksw.rdfunit.tests;

import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import java.nio.file.Paths;

public class W3CShaclTest {

    @Ignore // these tests were splitted up into single tests (see other W3C test classes)
    @Test
    public void runW3CTests() {
        val rootManifestPath = Paths.get("tests/manifest.ttl");
        val saveExpectedVsActualResults = true;

        val suite = W3CShaclTestSuite.load(rootManifestPath);

        //suite.getTestCases().parallelStream().forEach(TestCase::getExecution);
        val failureCount = suite.getTestCases().stream().filter(t -> t.getExecution().isFailure()).count();

        val hist = List.ofAll(suite.getTestCases()).groupBy(tc -> tc.computeOutcome(saveExpectedVsActualResults)).mapValues(Traversable::size);

        System.out.println(hist);
    }
}
