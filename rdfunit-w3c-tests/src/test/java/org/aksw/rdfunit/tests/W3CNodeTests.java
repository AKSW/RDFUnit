package org.aksw.rdfunit.tests;

import lombok.val;
import org.junit.Assert;

import java.nio.file.Paths;

public class W3CNodeTests {

    private boolean saveExpectedVsActualResults = false;
    
    @org.junit.Test
    public void andTests(){

        val rootManifestPath = Paths.get("tests/core/node/and-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void andTests2(){

        val rootManifestPath = Paths.get("tests/core/node/and-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void classTests(){

        val rootManifestPath = Paths.get("tests/core/node/class-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void classTests2(){

        val rootManifestPath = Paths.get("tests/core/node/class-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void classTests3(){

        val rootManifestPath = Paths.get("tests/core/node/class-003.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void closedTests(){

        val rootManifestPath = Paths.get("tests/core/node/closed-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void closedTests2(){

        val rootManifestPath = Paths.get("tests/core/node/closed-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void datatypeTests(){

        val rootManifestPath = Paths.get("tests/core/node/datatype-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void datatypeTests2(){

        val rootManifestPath = Paths.get("tests/core/node/datatype-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void disjointTests(){

        val rootManifestPath = Paths.get("tests/core/node/disjoint-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
        Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void equalsTests(){

        val rootManifestPath = Paths.get("tests/core/node/equals-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void hasValueTests(){

        val rootManifestPath = Paths.get("tests/core/node/hasValue-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void inTests(){

        val rootManifestPath = Paths.get("tests/core/node/in-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void languageInTests(){

        val rootManifestPath = Paths.get("tests/core/node/languageIn-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void maxExclusiveTests(){

        val rootManifestPath = Paths.get("tests/core/node/maxExclusive-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void maxInclusiveTests(){

        val rootManifestPath = Paths.get("tests/core/node/maxInclusive-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void maxLengthTests(){

        val rootManifestPath = Paths.get("tests/core/node/maxLength-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void minExclusiveTests(){

        val rootManifestPath = Paths.get("tests/core/node/minExclusive-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void minInclusiveTests(){

        val rootManifestPath = Paths.get("tests/core/node/minInclusive-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void minInclusiveTests2(){

        val rootManifestPath = Paths.get("tests/core/node/minInclusive-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void minInclusiveTests3(){

        val rootManifestPath = Paths.get("tests/core/node/minInclusive-003.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void minLengthTests(){

        val rootManifestPath = Paths.get("tests/core/node/minLength-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void nodeTests(){

        val rootManifestPath = Paths.get("tests/core/node/node-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void nodeKindTests(){

        val rootManifestPath = Paths.get("tests/core/node/nodeKind-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void notTests(){

        val rootManifestPath = Paths.get("tests/core/node/not-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void notTests2(){

        val rootManifestPath = Paths.get("tests/core/node/not-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void orTests(){

        val rootManifestPath = Paths.get("tests/core/node/or-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void patternTests(){

        val rootManifestPath = Paths.get("tests/core/node/pattern-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void patternTests2(){

        val rootManifestPath = Paths.get("tests/core/node/pattern-002.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void qualifiedTests(){

        val rootManifestPath = Paths.get("tests/core/node/qualified-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void xoneTests(){

        val rootManifestPath = Paths.get("tests/core/node/xone-001.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }

    @org.junit.Test
    public void xoneDuplicateTests(){

        val rootManifestPath = Paths.get("tests/core/node/xone-duplicate.ttl");
        val suite = W3CShaclTestSuite.load(rootManifestPath);
                Assert.assertTrue(suite.runTestSuite(saveExpectedVsActualResults));

    }
}
