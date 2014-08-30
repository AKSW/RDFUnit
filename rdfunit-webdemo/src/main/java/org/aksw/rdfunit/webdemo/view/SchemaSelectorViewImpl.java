package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.aksw.rdfunit.webdemo.utils.SchemaOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:25 PM
 */
public class SchemaSelectorViewImpl extends CustomComponent implements SchemaSelectorView{

    private final List<SchemaSelectorViewListener> listeners =
            new ArrayList<SchemaSelectorViewListener>();

    private final NativeSelect inputFormatsSelect = new NativeSelect("Select Input Format");
    private final OptionGroup inputTypeSelect = new OptionGroup("Select Input Type");
    private final TextArea inputText = new TextArea();
    private final Label messageLabel = new Label();
    private final Button clearButton = new Button("Clear");
    private final Button continueButton = new Button("Load");



    public SchemaSelectorViewImpl(){
        initLayout();

    }


    private void initLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);

        HorizontalLayout components = new HorizontalLayout();
        components.setSpacing(true);




        setDefaultValues();

        setCompositionRoot(root);


        clearButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                setDefaultValues();
            }
        });

        continueButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                for (SchemaSelectorViewListener listener : listeners) {

                    //listener.SchemaIsSet(isText, text, format);
                }
            }
        });

    }


    @Override
    public void addListener(SchemaSelectorViewListener listener) {
        listeners.add(listener);
    }

    @Override
    public void setMessage(String message, boolean isError) {
        messageLabel.setValue(message);
        if (isError) {
            messageLabel.setStyleName(ValoTheme.LABEL_FAILURE);
        }
        else {
            messageLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
        }

    }

    private void setDefaultValues() {
        messageLabel.setValue("SPARQL Endpoints are excluded from the demo to prevent abuse...");
        messageLabel.setStyleName(ValoTheme.LABEL_LIGHT);
        inputFormatsSelect.setValue("turtle");
        inputTypeSelect.setValue("custom");

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
        inputTypeSelect.addItem(SchemaOption.AUTO_OWL);
        inputTypeSelect.setItemCaption(SchemaOption.AUTO_OWL, "Automatic (OWL)");
        inputTypeSelect.addItem(SchemaOption.SPECIFIC_URIS);
        inputTypeSelect.setItemCaption(SchemaOption.SPECIFIC_URIS, "Specific Schemas");
        inputTypeSelect.addItem(SchemaOption.CUSTOM_TEXT);
        inputTypeSelect.setItemCaption(SchemaOption.CUSTOM_TEXT, "Direct Input");

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

        inputText.setCaption("Direct Constraints Input");
        inputText.setInputPrompt("Paste constraints in RDF directly here.\n" +
                "Constraints can be in the form of:\n" +
                "1) OWL axioms (e.g. rdfs:range / rdfs:domain / cardinality constraints e.t.c.)\n" +
                "2) DSP Profile rules ()\n" +
                "3) IBM Resource (typed) Shapes " +
                "\nhttp://example.com/1 (dereference)\n" +
                "http://example.com/file1.nt (download)\n\n" +
                "Note that there is a limit of 10MB for remote resources and compressed files are not supported");

        inputText.setRows(8);
        inputText.setColumns(40);
        inputText.setWidth("100%");
    }


}
