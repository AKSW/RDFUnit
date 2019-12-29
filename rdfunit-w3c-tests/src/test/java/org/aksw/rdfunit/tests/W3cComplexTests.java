package org.aksw.rdfunit.tests;

import lombok.val;
import org.junit.Assert;

import java.nio.file.Paths;

public class W3cComplexTests {

    private boolean saveExpectedVsActualResults = false;

    @org.junit.Test
    public void shaclShaclTest(){

        val rootManifestPath = Paths.get("tests/core/complex/shacl-shacl.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));
    }

    @org.junit.Test
    public void personsTest(){

        val rootManifestPath = Paths.get("tests/core/complex/personexample.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }
}
