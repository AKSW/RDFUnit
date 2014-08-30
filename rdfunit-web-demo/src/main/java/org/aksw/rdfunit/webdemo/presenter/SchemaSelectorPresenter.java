package org.aksw.rdfunit.webdemo.presenter;

import org.aksw.rdfunit.webdemo.view.SchemaSelectorView;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:24 PM
 */
public class SchemaSelectorPresenter implements SchemaSelectorView.SchemaSelectorViewListener {

    private final SchemaSelectorView schemaSelectorView;

    private static final long fileLimit = 10*1024*1024;

    public SchemaSelectorPresenter(SchemaSelectorView schemaSelectorView) {
        this.schemaSelectorView = schemaSelectorView;

        schemaSelectorView.addListener(this);
    }

    @Override
    public void schemaIsSet(boolean isText, String text, String format) {




    }
}
