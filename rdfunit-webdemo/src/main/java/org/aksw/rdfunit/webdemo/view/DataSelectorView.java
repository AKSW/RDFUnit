package org.aksw.rdfunit.webdemo.view;

import org.aksw.rdfunit.webdemo.utils.DataOption;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:19 PM
 */
public interface DataSelectorView {


    public interface DataSelectorViewListener {
        public boolean sourceIsSet(DataOption dataOption, String text, String format);
    }

    public void addListener(DataSelectorViewListener listener);

    public void setMessage(String message, boolean isError);
}
