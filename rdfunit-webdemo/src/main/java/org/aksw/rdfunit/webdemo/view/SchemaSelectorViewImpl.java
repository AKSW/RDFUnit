package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.aksw.rdfunit.webdemo.components.SchemaSelectorComponent;
import org.aksw.rdfunit.webdemo.utils.SchemaOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:25 PM
 */
public class SchemaSelectorViewImpl extends CustomComponent implements SchemaSelectorView, WorkflowItem{

    private final List<SchemaSelectorViewListener> listeners =
            new ArrayList<SchemaSelectorViewListener>();

    private final NativeSelect inputFormatsSelect = new NativeSelect("Select Input Format");
    private final OptionGroup inputTypeSelect = new OptionGroup("Constraints Input Type");
    private final TextArea inputText = new TextArea();
    private final Label messageLabel = new Label();
    private final Button clearButton = new Button("Clear");
    private final Button continueButton = new Button("Load");

    private final Label autoOWLMessage = new Label();
    private final Label specificSchemasMessage = new Label();

    private final SchemaSelectorComponent schemaSelectorWidget = new SchemaSelectorComponent();

    private WorkflowItem previous;
    private WorkflowItem next;

    private volatile boolean isReady = false;



    public SchemaSelectorViewImpl(){
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

        VerticalLayout optionSelection = new VerticalLayout();
        optionSelection.setSpacing(true);
        optionSelection.setWidth("200px");

        optionSelection.addComponent(inputTypeSelect);
        optionSelection.addComponent(inputFormatsSelect);

        VerticalLayout optionValues = new VerticalLayout();
        optionSelection.setSpacing(true);

        optionValues.addComponent(autoOWLMessage);
        optionValues.addComponent(specificSchemasMessage);
        optionValues.addComponent(schemaSelectorWidget);
        schemaSelectorWidget.setWidth("50%");
        optionValues.addComponent(inputText);

        components.addComponent(optionSelection);
        components.addComponent(optionValues);
        components.setExpandRatio(optionValues, 1.0f);

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
                isReady = false;

                WorkflowItem p = getPreviousItem();
                if (p != null && !p.isReady()) {
                    setMessage("Please Complete previous step correctly", true);
                }
                else {
                    for (SchemaSelectorViewListener listener : listeners) {
                        SchemaOption option = (SchemaOption) inputTypeSelect.getValue();

                        listener.schemaIsSet(option, schemaSelectorWidget.getSelections(), inputText.getValue(), inputFormatsSelect.getValue().toString());
                    }
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
            isReady = false;
        }
        else {
            messageLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
            isReady = true;
        }

    }

    private void setDefaultValues() {
        messageLabel.setValue("Note that we do not provide full OWL/DSP/RS (but will in time) support see our <a href='https://github.com/AKSW/RDFUnit/wiki' target=\"_blank\">wiki</a> " +
                "and <a href='https://github.com/AKSW/RDFUnit/issues' target=\"_blank\">issues</a> for details.");
        messageLabel.setStyleName(ValoTheme.LABEL_LIGHT);
        inputFormatsSelect.setValue("turtle");
        inputTypeSelect.setValue(SchemaOption.AUTO_OWL);

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
        inputTypeSelect.setItemCaption(SchemaOption.AUTO_OWL, "Automatic (RDFS/OWL)");
        inputTypeSelect.addItem(SchemaOption.SPECIFIC_URIS);
        inputTypeSelect.setItemCaption(SchemaOption.SPECIFIC_URIS, "Specific Schemas");
        inputTypeSelect.addItem(SchemaOption.CUSTOM_TEXT);
        inputTypeSelect.setItemCaption(SchemaOption.CUSTOM_TEXT, "Direct Input");

        inputTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {

                SchemaOption option = (SchemaOption) valueChangeEvent.getProperty().getValue();

                switch (option) {
                    case AUTO_OWL:
                        inputFormatsSelect.setVisible(false);
                        specificSchemasMessage.setVisible(false);
                        schemaSelectorWidget.setVisible(false);
                        inputText.setVisible(false);

                        autoOWLMessage.setVisible(true);

                        break;

                    case SPECIFIC_URIS:
                        inputFormatsSelect.setVisible(false);
                        inputText.setVisible(false);
                        autoOWLMessage.setVisible(false);

                        specificSchemasMessage.setVisible(true);
                        schemaSelectorWidget.setVisible(true);


                        break;
                    case CUSTOM_TEXT:
                        inputFormatsSelect.setVisible(false);
                        specificSchemasMessage.setVisible(false);
                        schemaSelectorWidget.setVisible(false);
                        autoOWLMessage.setVisible(false);

                        inputText.setVisible(true);

                        break;
                }
            }
        });
    }

    private void setInputText() {

        inputText.setCaption("Direct Constraints Input");
        inputText.setInputPrompt("Paste constraints in RDF directly here.\n" +
                "Constraints can be in the form of:\n" +
                "1) RDFS/OWL axioms (e.g. rdfs:range / rdfs:domain / cardinality constraints e.t.c.)\n" +
                "2) DSP Profile rules (http://dublincore.org/documents/2008/03/31/dc-dsp/)\n" +
                "3) IBM Resource (typed) Shapes (http://www.w3.org/Submission/2014/SUBM-shapes-20140211/)\n" +
                "(We do not yet support SPIN rules but will do soon)");

        inputText.setRows(8);
        inputText.setColumns(40);
        inputText.setWidth("100%");

        autoOWLMessage.setContentMode(ContentMode.HTML);
        autoOWLMessage.setValue("<h3>Automatic Constraint Detection based on RDFS/OWL</h3>" +
                "We will parse the input source and identify all used properties and classes.<br/>" +
                "Based on them we will try to dereference all mentions vocabularies & ontologies.<br/>" +
                "Then we will use these vocabularies in our algorithms to generate automatic RDFUnit Test Cases.<br/>" +
                "<b>Note that atm (for safety reasons) we automatically dereference only schemas that are available in LOV</b>");

        specificSchemasMessage.setContentMode(ContentMode.HTML);
        specificSchemasMessage.setValue("<h3>Set Specific Schema Constraints</h3>" +
                "Schemas from LOV are available with auto-complete and will be used for RDFS/OWL checking. " +
                "You can also paste a <b>custom URI</b> and press \"Enter\" to insert it.<br/>" +
                "Note that custom URIs <b>can contain OWL/RDFS, <a href=\"http://dublincore.org/documents/2008/03/31/dc-dsp/\" target=\"_blank\">DSP Constraints</a> " +
                "or <a href=\"http://www.w3.org/Submission/2014/SUBM-shapes-20140211/\" target=\"_blank\">Resource (typed) Shapes</a> constraints</b> (SPIN is not yet supported).<br/>" +
                "Even if you mix different types of constraints  RDFUnit will still work but the result will have to make sense ;)<br/>&nbsp;");
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
