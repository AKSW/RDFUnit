package org.aksw.rdfunit.webdemo.view;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * <p>EndointTestTab class.</p>
 *
 * @author Dimitris Kontokostas
 *         This is a placeholder to keep the main content components
 *         TODO everything is clattered in here need to separate many things
 * @since 11/15/13 8:19 AM
 * @version $Id: $Id
 */
public class EndpointTestTab extends VerticalLayout {


    //    private final NativeSelect examplesSelect = new NativeSelect("Select an example");
//    private final TextField endpointField = new TextField();
//    private final TextField graphField = new TextField();
//    private final SchemaSelectorComponent schemaSelectorWidget = new SchemaSelectorComponent();


    //    private final NativeSelect limitSelect = new NativeSelect();
    //private final Button clearButton = new Button("Clear");



    /**
     * <p>Constructor for EndointTestTab.</p>
     */
    public EndpointTestTab() {
        initLayout();
    }

    private void initLayout() {
        this.setMargin(true);
        this.setId("EndointTestTab");
        this.setWidth("100%");

        this.addComponent(new Label("<h2>1. Data Selection</h2>", ContentMode.HTML));

        // Create the model and the Vaadin view implementation
        DataSelectorView dataSelectorView = new DataSelectorView();
        this.addComponent(dataSelectorView);

        this.addComponent(new Label("<h2>2. Constraints Selection</h2>", ContentMode.HTML));
        SchemaSelectorView schemaSelectorView = new SchemaSelectorView();
        this.addComponent(schemaSelectorView);

        this.addComponent(new Label("<h2>3. Test Generation</h2>", ContentMode.HTML));

        TestGenerationView testGenerationView = new TestGenerationView();
        this.addComponent(testGenerationView);

        this.addComponent(new Label("<h2>4. Testing</h2>", ContentMode.HTML));
        TestExecutionView testExecutionView = new TestExecutionView();
        this.addComponent(testExecutionView);

        // Set previous / next
        dataSelectorView.setNextItem(schemaSelectorView);

        schemaSelectorView.setPreviousItem(dataSelectorView);
        schemaSelectorView.setNextItem(testGenerationView);

        testGenerationView.setPreviousItem(schemaSelectorView);
        testGenerationView.setNextItem(testExecutionView);

        testExecutionView.setPreviousItem(testGenerationView);
        //testGenerationView.setNextItem();


    }


}
