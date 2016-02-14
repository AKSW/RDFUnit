package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.io.reader.RdfDereferenceLimitReader;
import org.aksw.rdfunit.utils.StringUtils;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.CommonAccessUtils;
import org.aksw.rdfunit.webdemo.utils.DataOption;
import org.aksw.rdfunit.webdemo.utils.WorkflowUtils;

import java.net.URL;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:25 PM
 */
final class DataSelectorView extends CustomComponent implements WorkflowItem {

    private static final long fileLimit = 10 * 1024 * 1024;

    private final NativeSelect inputFormatsSelect = new NativeSelect("Select Input Format");
    private final OptionGroup inputTypeSelect = new OptionGroup("Select Input Type");
    private final TextArea inputText = new TextArea();
    private final Label messageLabel = new Label();

    private final Button clearBtn = new Button("Clear");
    private final Button loadBtn = new Button("Load");

    private WorkflowItem previous;
    private WorkflowItem next;

    private volatile boolean isReady = false;


    /**
     * <p>Constructor for DataSelectorView.</p>
     */
    public DataSelectorView() {
        initLayout();
    }

    private void initLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);

        HorizontalLayout components = new HorizontalLayout();
        components.setSpacing(true);
        components.setWidth("100%");


        setInputTypes();
        setInputFormats();
        setInputText();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setWidth("200px");

        verticalLayout.addComponent(inputTypeSelect);
        verticalLayout.addComponent(inputFormatsSelect);

        components.addComponent(verticalLayout);
        components.addComponent(inputText);
        components.setExpandRatio(inputText, 1.0f);

        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setWidth("100%");
        bottomLayout.setSpacing(true);
        bottomLayout.addComponent(messageLabel);
        bottomLayout.setExpandRatio(messageLabel, 1.0f);
        messageLabel.setContentMode(ContentMode.HTML);

        bottomLayout.addComponent(clearBtn);
        bottomLayout.addComponent(loadBtn);


        root.addComponent(components);
        root.addComponent(bottomLayout);

        setDefaultValues();

        setCompositionRoot(root);


        clearBtn.addClickListener((Button.ClickListener) clickEvent -> {
            isReady = false;
            setDefaultValues();
        });

        loadBtn.addClickListener((Button.ClickListener) clickEvent -> UI.getCurrent().access(() -> {
            DataSelectorView.this.loadBtn.setEnabled(false);
            setMessage("Loading...", false);
            CommonAccessUtils.pushToClient();
            DataSelectorView.this.execute();
            DataSelectorView.this.loadBtn.setEnabled(true);
            CommonAccessUtils.pushToClient();
        }));

    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(String message, boolean isError) {

        this.isReady = !isError;
        WorkflowUtils.setMessage(messageLabel, message, isError);
    }

    private void setDefaultValues() {
        messageLabel.setValue("SPARQL Endpoints are excluded from the demo to prevent abuse");
        messageLabel.setStyleName(ValoTheme.LABEL_LIGHT);
        inputFormatsSelect.setValue("turtle");
        inputTypeSelect.setValue(DataOption.TEXT);
        inputText.setValue("");
    }

    private void setInputFormats() {
        inputFormatsSelect.addItem("turtle");
        inputFormatsSelect.setItemCaption("turtle", "Turtle");
        inputFormatsSelect.addItem("ntriples");
        inputFormatsSelect.setItemCaption("ntriples", "N-Triples");
        inputFormatsSelect.addItem("n3");
        inputFormatsSelect.setItemCaption("n3", "N3");
        inputFormatsSelect.addItem("jsonld");
        inputFormatsSelect.setItemCaption("jsonld", "JSON-LD");
        inputFormatsSelect.addItem("rdfjson");
        inputFormatsSelect.setItemCaption("rdfjson", "RDF/JSON");
        inputFormatsSelect.addItem("rdfxml");
        inputFormatsSelect.setItemCaption("rdfxml", "RDF/XML");

        // Select turtle
        inputFormatsSelect.setNullSelectionAllowed(false);


    }

    private void setInputTypes() {
        inputTypeSelect.addItem(DataOption.SPARQL);
        inputTypeSelect.setItemCaption(DataOption.SPARQL, "SPARQL");
        inputTypeSelect.addItem(DataOption.URI);
        inputTypeSelect.setItemCaption(DataOption.URI, "Remote Files");
        inputTypeSelect.addItem(DataOption.TEXT);
        inputTypeSelect.setItemCaption(DataOption.TEXT, "Direct Input");

        inputTypeSelect.setItemEnabled(DataOption.SPARQL, false);

        inputTypeSelect.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            DataOption value = (DataOption) valueChangeEvent.getProperty().getValue();
            if (value.equals(DataOption.URI)) {
                inputFormatsSelect.setVisible(false);
            } else {
                inputFormatsSelect.setVisible(true);
            }
        });
    }

    private void setInputText() {

        inputText.setCaption("Input Data or URL / IRI");
        inputText.setInputPrompt("Either paste RDF directly here or place a URL / IRI. e.g.: " +
                "\nhttp://example.com/1 (dereference)\n" +
                "http://example.com/file1.nt (download)\n\n" +
                "Note that there is a limit of 10MB for remote resources and compressed files are not supported");

        inputText.setRows(8);
        inputText.setColumns(40);
        inputText.setWidth("100%");
    }


    /** {@inheritDoc} */
    @Override
    public boolean isReady() {
        return isReady;
    }

    /** {@inheritDoc} */
    @Override
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    /** {@inheritDoc} */
    @Override
    public void setPreviousItem(WorkflowItem item) {
        previous = item;
    }

    /** {@inheritDoc} */
    @Override
    public void setNextItem(WorkflowItem item) {
        next = item;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowItem getPreviousItem() {
        return previous;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowItem getNextItem() {
        return next;
    }

    /** {@inheritDoc} */
    @Override
    public boolean execute() {
        DataSelectorView.this.setMessage("Loading...", false);
        DataSelectorView.this.setReady(false);

        DataOption dataOption = (DataOption) inputTypeSelect.getValue();
        String text = inputText.getValue();
        String format = inputFormatsSelect.getValue().toString();


        String uri;
        if (dataOption.equals(DataOption.URI)) {
            uri = text.trim();
        }
        else {
            uri = "http://rdfunit.aksw.org/CustomSource#"+ StringUtils.getHashFromString(text);
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
                if (RdfDereferenceLimitReader.getUriSize(uri) > fileLimit)
                    throw new Exception("Contents of " + uri + " bigger than 10MB");
                configuration.setCustomDereferenceURI(uri);

            } else {
                configuration.setCustomTextSource(text, format);

            }
            // Try to load it for errors
            QueryExecutionFactoryModel qefm = (QueryExecutionFactoryModel) configuration.getTestSource().getExecutionFactory();
            if (qefm.getModel().isEmpty()) {
                this.setMessage("No data found", true);
                return false;
            }

            // If successful add it in session
            DataSelectorView.this.setMessage("Data loaded successfully! (" + qefm.getModel().size() + " statements)", false);
            RDFUnitDemoSession.setRDFUnitConfiguration(configuration);
            return true;

        } catch (Exception e) {
            DataSelectorView.this.setMessage("Error: " + e.getMessage(), true);
            return false;
        }
    }
}
