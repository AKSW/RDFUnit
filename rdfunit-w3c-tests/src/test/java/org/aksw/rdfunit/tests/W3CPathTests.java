package org.aksw.rdfunit.tests;

import lombok.val;
import org.junit.Assert;

import java.nio.file.Paths;

public class W3CPathTests {

    private boolean saveExpectedVsActualResults = false;

    @org.junit.Test
    public void pathAlternativeTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-alternative-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathComplexTests1(){

        val rootManifestPath = Paths.get("tests/core/path/path-complex-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathComplexTests2(){

        val rootManifestPath = Paths.get("tests/core/path/path-complex-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathInverseTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-inverse-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathOneOrMoreTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-oneOrMore-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathSequenceTests1(){

        val rootManifestPath = Paths.get("tests/core/path/path-sequence-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathSequenceTests2(){

        val rootManifestPath = Paths.get("tests/core/path/path-sequence-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathSequenceDuplicateTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-sequence-duplicate-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathStrangeTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-strange-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathStrangeTests2(){

        val rootManifestPath = Paths.get("tests/core/path/path-strange-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathUnusedTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-unused-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathZeroOrMoreTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-zeroOrMore-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void pathZeroOrOneTests(){

        val rootManifestPath = Paths.get("tests/core/path/path-zeroOrOne-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }
}
