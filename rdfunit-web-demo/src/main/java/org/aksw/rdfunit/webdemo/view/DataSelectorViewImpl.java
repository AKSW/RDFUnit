package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Property;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:25 PM
 */
public class DataSelectorViewImpl extends CustomComponent implements DataSelectorView{

    private final List<DataSelectorViewListener> listeners =
            new ArrayList<DataSelectorViewListener>();

    private final NativeSelect inputFormatsSelect = new NativeSelect("Select Input Format");
    private final OptionGroup inputTypeSelect = new OptionGroup("Select Input Type");
    private final TextArea inputText = new TextArea();
    private final Label message = new Label("SPARQL Endpoints are excluded from the demo to prevent abuse...");
    private final Button clearButton = new Button("Clear");
    private final Button continueButton = new Button("Continue");


    public DataSelectorViewImpl(){
        initLayout();

    }


    private void initLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);

        HorizontalLayout components = new HorizontalLayout();
        components.setSpacing(true);


        setInputTypes();
        setInputFormats();
        setInputText();

        VerticalLayout verticalLayout = new VerticalLayout();


        verticalLayout.addComponent(inputTypeSelect);
        verticalLayout.addComponent(inputFormatsSelect);

        components.addComponent(verticalLayout);
        components.addComponent(inputText);

        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setSpacing(true);
        bottomLayout.addComponent(message);
        bottomLayout.setExpandRatio(message, 1.0f);
        bottomLayout.addComponent(clearButton);
        bottomLayout.addComponent(continueButton);


        root.addComponent(components);
        root.addComponent(bottomLayout);

        setCompositionRoot(root);


        clearButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                inputText.setValue("");
                inputFormatsSelect.setValue("turtle");
                inputTypeSelect.setValue("text");
            }
        });

        continueButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                for (DataSelectorViewListener listener : listeners) {
                    boolean isText = inputTypeSelect.getValue().equals("text");
                    String text = inputText.getValue();
                    String format = inputFormatsSelect.getValue().toString();
                    listener.sourceIsSet(isText, text, format);
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
        inputFormatsSelect.setValue("turtle");


    }

    private void setInputTypes() {
        inputTypeSelect.addItem("urls");
        inputTypeSelect.setItemCaption("urls", "Remote Files");
        inputTypeSelect.addItem("text");
        inputTypeSelect.setItemCaption("text", "Direct Input");

        inputTypeSelect.setValue("text");

        inputTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                String value = valueChangeEvent.getProperty().getValue().toString();
                if (value.equals("urls")) {
                    inputFormatsSelect.setVisible(false);
                }
                else {
                    inputFormatsSelect.setVisible(true);
                }
            }
        });
    }

    private void setInputText() {

        inputText.setCaption("Input Data or URLs / IRIs");
        inputText.setInputPrompt("Either paste RDF directly here or place one URL / IRI per line. e.g.: " +
                "\nhttp://example.com/1\n" +
                "http://example.com/file1.nt\n" +
                "http://example.com/file2.ttl\n\n" +
                "There is a limit of 5 URLs and each one cannot be larger that 10MB");

        inputText.setRows(8);
        inputText.setColumns(40);
        inputText.setWidth("100%");
    }


}
