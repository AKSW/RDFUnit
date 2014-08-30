package org.aksw.rdfunit.webdemo.presenter;

import org.aksw.rdfunit.webdemo.model.DataSelectorModel;
import org.aksw.rdfunit.webdemo.view.DataSelectorView;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:24 PM
 */
public class DataSelectorPresenter implements DataSelectorView.DataSelectorViewListener {

    private final DataSelectorModel dataSelectorModel;
    private final DataSelectorView dataSelectorView;

    public DataSelectorPresenter(DataSelectorModel dataSelectorModel, DataSelectorView dataSelectorView) {
        this.dataSelectorModel = dataSelectorModel;
        this.dataSelectorView = dataSelectorView;

        dataSelectorView.addListener(this);
    }

    @Override
    public void sourceIsSet(boolean isText, String text, String format) {

    }
}
