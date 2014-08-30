package org.aksw.rdfunit.webdemo.view;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:19 PM
 */
public interface DataSelectorView {


    public interface DataSelectorViewListener {
        public void sourceIsSet(boolean isText, String text, String format);
    }

    public void addListener(DataSelectorViewListener listener);

    public void setMessage(String message, boolean isError);
}
