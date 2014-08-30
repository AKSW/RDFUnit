package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.aksw.rdfunit.webdemo.utils.DataOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:25 PM
 */
public class DataSelectorViewImpl extends CustomComponent implements DataSelectorView, WorkflowItem {

    private final List<DataSelectorViewListener> listeners =
            new ArrayList<DataSelectorViewListener>();

    private final NativeSelect inputFormatsSelect = new NativeSelect("Select Input Format");
    private final OptionGroup inputTypeSelect = new OptionGroup("Select Input Type");
    private final TextArea inputText = new TextArea();
    private final Label messageLabel = new Label();
    private final Button clearButton = new Button("Clear");
    private final Button continueButton = new Button("Load");

    private WorkflowItem previous;
    private WorkflowItem next;

    private volatile boolean isReady = false;


    public DataSelectorViewImpl(){
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

        bottomLayout.addComponent(clearButton);
        bottomLayout.addComponent(continueButton);


        root.addComponent(components);
        root.addComponent(bottomLayout);

        setDefaultValues();

        setCompositionRoot(root);


        clearButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                isReady = false;
                setDefaultValues();
            }
        });

        continueButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                messageLabel.setValue("Loading...");
                messageLabel.setStyleName(ValoTheme.LABEL_SPINNER);

                isReady = false;

                DataSelectorViewImpl.this.setEnabled(false);

                for (DataSelectorViewListener listener : listeners) {


                    DataOption dataOption = (DataOption) inputTypeSelect.getValue();
                    String text = inputText.getValue();
                    String format = inputFormatsSelect.getValue().toString();
                    listener.sourceIsSet(dataOption, text, format);
                }
            }
        });

    }


    @Override
    public void addListener(DataSelectorViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void setMessage(String message, boolean isError) {
        DataSelectorViewImpl.this.setEnabled(true);
        messageLabel.setValue(message);
        if (isError) {
            messageLabel.setStyleName(ValoTheme.LABEL_FAILURE);
            isReady = false;
        }
        else {
            messageLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
            isReady = true;
        }

    }

    private void setDefaultValues() {
        messageLabel.setValue("SPARQL Endpoints are excluded from the demo to prevent abuse...");
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

        inputTypeSelect.setItemEnabled(DataOption.SPARQL,false);

        inputTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String value = valueChangeEvent.getProperty().getValue().toString();
                if (value.equals(DataOption.URI)) {
                    inputFormatsSelect.setVisible(false);
                }
                else {
                    inputFormatsSelect.setVisible(true);
                }
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


    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public void setPreviousItem(WorkflowItem item) {
        previous = item;
    }

    @Override
    public void setNextItem(WorkflowItem item) {
        next = item;
    }

    @Override
    public WorkflowItem getPreviousItem() {
        return previous;
    }

    @Override
    public WorkflowItem getNextItem() {
        return next;
    }
}
