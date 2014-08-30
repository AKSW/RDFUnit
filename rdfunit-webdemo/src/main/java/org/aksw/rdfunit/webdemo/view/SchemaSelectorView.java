package org.aksw.rdfunit.webdemo.view;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:19 PM
 */
public interface SchemaSelectorView {


    public interface SchemaSelectorViewListener {
        public void schemaIsSet(boolean isText, String text, String format);
    }

    public void addListener(SchemaSelectorViewListener listener);

    public void setMessage(String message, boolean isError);
}
