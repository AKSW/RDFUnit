package org.aksw.rdfunit.webdemo.presenter;

import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.webdemo.utils.SchemaOption;
import org.aksw.rdfunit.webdemo.view.TestGenerationView;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:24 PM
 */
public class TestGenerationPresenter implements TestGenerationView.TestGenerationViewListener {

    private final TestGenerationView testGenerationView;

    private static final long fileLimit = 10*1024*1024;

    public TestGenerationPresenter(TestGenerationView testGenerationView) {
        this.testGenerationView = testGenerationView;

        testGenerationView.addListener(this);
    }

    @Override
    public boolean schemaIsSet(SchemaOption schemaOption, Collection<SchemaSource> schemaSources, String text, String format) {

        return false;

    }
}
