package org.aksw.rdfunit.webdemo.presenter;

import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.io.RDFDereferenceLimitReader;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.DataOption;
import org.aksw.rdfunit.webdemo.view.DataSelectorView;

import java.net.URL;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:24 PM
 */
public class DataSelectorPresenter implements DataSelectorView.DataSelectorViewListener {

    private final DataSelectorView dataSelectorView;

    private static final long fileLimit = 10*1024*1024;

    public DataSelectorPresenter(DataSelectorView dataSelectorView) {
        this.dataSelectorView = dataSelectorView;

        dataSelectorView.addListener(this);
    }

    @Override
    public void sourceIsSet(DataOption dataOption, String text, String format) {

        String uri = "http://rdfunit.aksw.org";
        if (dataOption.equals(DataOption.URI)) {
            uri = text.trim();
        }
        RDFUnitConfiguration configuration = new RDFUnitConfiguration(uri, RDFUnitDemoSession.getBaseDir());

        try {
            if (text.trim().isEmpty()) {
                throw new Exception("Empty Data");
            }
            if (dataOption.equals(DataOption.URI)) {

                // Check if valid URI
                new URL(uri);

                // Check size
                if (RDFDereferenceLimitReader.getUriSize(uri) > fileLimit)
                    throw new Exception("Contents of " + uri + " bigger than 10MB");
                configuration.setCustomDereferenceURI(uri);

                // Try to load it for errors
                configuration.getTestSource().getExecutionFactory();
            }
            else {
                configuration.setCustomTextSource(text, format);
            }
            // If successful add it in session
            dataSelectorView.setMessage("Data loaded successfully!", false);
            RDFUnitDemoSession.setRDFUnitConfiguration(configuration);

        }
        catch (Exception e) {
            dataSelectorView.setMessage("Error: " + e.getMessage(), true);

        }


    }
}
