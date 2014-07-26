package org.aksw.rdfunit.Utils;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.io.RDFReader;
import org.junit.Before;
import org.junit.Test;

//import static org.junit.Assert.*;

public class PatternUtilsTest {

    QueryExecutionFactory qef;

    @Before
    public void setUp() throws Exception {
        RDFReader reader = RDFUnitUtils.getPatternsFromResource();
        qef = new QueryExecutionFactoryModel(reader.read());
    }

    @Test
    public void testInstantiatePatternsFromModel() throws Exception {
        PatternUtils.instantiatePatternsFromModel(qef);
    }

    @Test
    public void testGetPattern() throws Exception {


    }
}