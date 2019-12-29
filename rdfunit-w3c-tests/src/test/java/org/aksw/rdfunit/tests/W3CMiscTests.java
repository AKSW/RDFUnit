package org.aksw.rdfunit.tests;

import lombok.val;
import org.junit.Assert;

import java.nio.file.Paths;

public class W3CMiscTests {

    private boolean saveExpectedVsActualResults = false;

    @org.junit.Test
    public void deactivatedTests(){

        val rootManifestPath = Paths.get("tests/core/misc/deactivated-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void deactivated2Tests(){

        val rootManifestPath = Paths.get("tests/core/misc/deactivated-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void messageTests(){

        val rootManifestPath = Paths.get("tests/core/misc/message-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void severityTests1(){

        val rootManifestPath = Paths.get("tests/core/misc/severity-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void severityTests2(){

        val rootManifestPath = Paths.get("tests/core/misc/severity-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }
}
