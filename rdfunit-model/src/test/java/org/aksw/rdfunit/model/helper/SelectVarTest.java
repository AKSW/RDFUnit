package org.aksw.rdfunit.model.helper;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SelectVarTest {


    @Test
    public void testCreate() {
        String varName = "test";
        SelectVar selectVar = SelectVar.create(varName);

        assertThat(selectVar.getName())
                .isEqualTo(varName);
        assertThat(selectVar.getLabel())
                .isEqualTo(varName);

        assertThat(selectVar.asString())
                .isEqualTo(" ?" + varName + " ");

    }

    @Test
    public void testCreate1() {
        String varName = "test";
        String label = "testLabel";
        SelectVar selectVar = SelectVar.create(varName, label);

        assertThat(selectVar.getName())
                .isEqualTo(varName);
        assertThat(selectVar.getLabel())
                .isEqualTo(label);

        assertThat(selectVar.asString())
                .isEqualTo(" ( ?" + varName + " AS ?" + label + " ) ");
    }

    @Test(expected=NullPointerException.class)
    public void testNull() {
        SelectVar.create(null);
    }
}