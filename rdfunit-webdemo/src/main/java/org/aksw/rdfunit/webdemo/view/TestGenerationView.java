package org.aksw.rdfunit.webdemo.view;

import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.webdemo.utils.SchemaOption;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:19 PM
 */
public interface TestGenerationView {


    public interface TestGenerationViewListener {
        public boolean schemaIsSet(SchemaOption schemaOption, Collection<SchemaSource> schemaSources, String text, String format);
    }

    public void addListener(TestGenerationViewListener listener);

    public void setMessage(String message, boolean isError);
}
