package org.aksw.rdfunit.model.helper;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 8:52 PM
 */
public class SelectVarTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {
        String varName = "test";
        SelectVar selectVar = SelectVar.create(varName);

        assertThat(selectVar.getName())
                .isEqualTo(varName);
        assertThat(selectVar.getLabel())
                .isEqualTo(varName);

        assertThat(selectVar.toString())
                .isEqualTo(" ?" + varName + " ");

    }

    @Test
    public void testCreate1() throws Exception {
        String varName = "test";
        String label = "testLabel";
        SelectVar selectVar = SelectVar.create(varName, label);

        assertThat(selectVar.getName())
                .isEqualTo(varName);
        assertThat(selectVar.getLabel())
                .isEqualTo(label);

        assertThat(selectVar.toString())
                .isEqualTo(" ( ?" + varName + " AS ?" + label + " ) ");
    }
}