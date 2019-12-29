package org.aksw.rdfunit.tests;

import com.google.common.collect.ImmutableList;
import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.jena.sparql.vocabulary.EARL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Markus Ackermann (including parts taken from Holger Knublauch)
 * @since 14/7/2017
 */

@RequiredArgsConstructor
@Slf4j
public class W3CShaclTestSuite {

    @Getter @NonNull @Singular ImmutableList<W3CTestCase> testCases;

    public boolean runTestSuite(boolean saveExpectedVsActualResults){
        val hist = List.ofAll(this.getTestCases()).groupBy(tc -> tc.computeOutcome(saveExpectedVsActualResults)).mapValues(Traversable::size);
        System.out.println(hist);
        return hist.get(EARL.passed).getOrElse(0) == this.getTestCases().size();
    }

    public static W3CShaclTestSuite load(Path rootManifest) {

        ImmutableList<W3CTestCase> tests = new W3CTestManifest(rootManifest).getTestCasesRecursive()
                .collect(ImmutableList.toImmutableList());

        return new W3CShaclTestSuite(tests);
    }

    public static void main(String[] args) throws IOException {

        val rootManifestPath = Paths.get("tests/manifest.ttl");
        val saveExpectedVsActualResults = true;

        log.info("{} test cases found.", new W3CTestManifest(rootManifestPath).getTestCasesRecursive().count());

        val suite = W3CShaclTestSuite.load(rootManifestPath); // saveExpectedVsActualResults == false => the difference is not saved as files but shown as std::out

        //suite.getTestCases().parallelStream().forEach(TestCase::getExecution);
        val failureCount = suite.getTestCases().stream().filter(t -> t.getExecution().isFailure()).count();

        log.info("{} tests had failures.", failureCount);

        FileUtils.deleteDirectory(new File(W3CTestManifest.resultFolder));  // delete result folder first
        val hist = List.ofAll(suite.getTestCases()).groupBy(x -> x.computeOutcome(saveExpectedVsActualResults)).mapValues(Traversable::size);

        log.info("EARL outcome histogram: {}", hist);
    }
}
