package org.aksw.databugger.ui;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.aksw.databugger.ui.view.EndointTestMainView;

import javax.servlet.annotation.WebServlet;
import java.util.HashMap;

/*
* User: Dimitris Kontokostas
*/
@SuppressWarnings("serial")
public class DatabuggerUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DatabuggerUI.class, widgetset = "org.aksw.databugger.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
    	/* User interface components are stored in session. */

    private final HorizontalLayout headerLayout = new HorizontalLayout();
    private final VerticalLayout sidebarLayout = new VerticalLayout();
    private final Panel  mainNavigator = new Panel();
    private final HorizontalLayout layoutFooter = new HorizontalLayout();

    private final Navigator navigator = new Navigator(this, mainNavigator);

    private TextField txtSchemaURI = new TextField("Schema URI","");
    private TextField txtSchemaDataURI = new TextField("Schema Data URI","");
    private CheckBox chkSchemaDifferent = new CheckBox("Different");
    private Button btnSchemaLoad = new Button("Load schema");
    private Model model = ModelFactory.createDefaultModel();

    private TabSheet tabsheet = new TabSheet();

    private TreeTable ttrClassTree = new TreeTable("Classes");
    private TreeTable ttrPropertyTree = new TreeTable("Properties");


    private String prefixes = "";



    /*
     * Any component can be bound to an external data source. This example uses
     * just a dummy in-memory list, but there are many more practical
     * implementations.
     */
    IndexedContainer contactContainer; // = createDummyDatasource();

    /*
     * After UI class is created, init() is executed. You should build and wire
     * up your user interface here.
     */
    protected void init(VaadinRequest request) {
        initLayout();
        initClassTree();
        initPropertyTree();

        txtSchemaURI.setValue("http://mappings.dbpedia.org/server/ontology/dbpedia.owl");
        txtSchemaDataURI.setValue("http://mappings.dbpedia.org/server/ontology/dbpedia.owl");


        prefixes += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        prefixes += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n";
        prefixes += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
        prefixes += "\n";
        prefixes += "\n";

        model.setNsPrefix("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs","http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("owl","http://www.w3.org/2002/07/owl#");
        model.setNsPrefix("foaf","http://xmlns.com/foaf/0.1/");
        //model.setNsPrefix("","");
        //model.setNsPrefix("","");
        //model.setNsPrefix("","");

    }

    /*
     * In this example layouts are programmed in Java. You may choose use a
     * visual editor, CSS or HTML templates for layout instead.
     */
    private void initLayout() {

        VerticalLayout page = new VerticalLayout();
        setContent(page);

        initLayoutHeader();
        page.addComponent(headerLayout);

        HorizontalLayout mainContent = new HorizontalLayout();
        page.addComponent(mainContent);

        initLayoutSidebar();
        mainContent.addComponent(sidebarLayout);

        initLayoutMain();
        mainContent.addComponent(mainNavigator);

        initLayoutFooter();
        page.addComponent(layoutFooter);

        navigator.addView("EndpointTest", new EndointTestMainView());
        navigator.navigateTo("EndpointTest");

        /*
        page.addComponent(txtSchemaURI);
        page.addComponent(txtSchemaDataURI);
        //txtSchemaDataURI.setEnabled(false);

        page.addComponent(btnSchemaLoad);

        page.addComponent(tabsheet);

        VerticalLayout tabClass = new VerticalLayout();
        tabsheet.addTab(tabClass,"Classes");
        tabClass.addComponent(ttrClassTree);

        VerticalLayout tabProperties = new VerticalLayout();
        tabsheet.addTab(tabProperties,"Properties");
        tabProperties.addComponent(ttrPropertyTree);

        btnSchemaLoad.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                loadSchema(txtSchemaDataURI.getValue());
                _populateClassTreeTable();
                _populatePropertyTreeTable();
            }
        });
        */

    }

    /*
    * setup the header of the page
    * */
    private void initLayoutHeader() {
        headerLayout.addComponent(new Label("Header"));
    }

    /*
    * setup the sidebar
    * */
    private void initLayoutSidebar() {
        sidebarLayout.addComponent(new Label("Sidebar"));
    }

    /*
    * setup the main content of the page
    * */
    private void initLayoutMain() {}

    /*
    * Setup the footer of the page
    * */
    private void initLayoutFooter() {
        layoutFooter.addComponent(new Label("Footer"));
    }

    private void initClassTree() {

        ttrClassTree.setSizeFull();
        ttrClassTree.setSelectable(true);

        ttrClassTree.addContainerProperty("Class", String.class, "");
        //ttrClassTree.addContainerProperty("subClassOf", String.class, "");

    }

    private void _populateClassTreeTable() {
        String getClassQuery = prefixes +
                " SELECT ?class ?subclass WHERE {" +
                "  ?class a owl:Class .\n" +
                " OPTIONAL { ?class rdfs:subClassOf ?subclass } }";

        Query q = QueryFactory.create(getClassQuery);
        QueryExecution qe = com.hp.hpl.jena.query.QueryExecutionFactory.create(q, model);
        ResultSet results = qe.execSelect();


        HashMap<String,Integer> classList = new HashMap<String, Integer>();
        HashMap<Integer,Integer> classHierarchy = new HashMap<Integer, Integer>();
        //int []hierarchy = new int[results.getRowNumber()*2+1];
        int currentClassIndex = 0;

        classList.put("http://www.w3.org/2002/07/owl#Thing", currentClassIndex++);

        while (results.hasNext()) {
            QuerySolution row= results.next();
            String cl= row.get("class").toString();
            String scl= row.contains("subclass") ? row.get("subclass").toString() : "http://www.w3.org/2002/07/owl#Thing";

            int clIndex, sclIndex;

            Object index = classList.get(scl);
            if (index == null ) {
                sclIndex = currentClassIndex;
                classList.put(scl,currentClassIndex++);
            } else {
                sclIndex = (Integer) index;
            }

            index = classList.get(cl);
            if (index == null ) {
                clIndex = currentClassIndex;
                classList.put(cl,currentClassIndex++);
            } else {
                clIndex = (Integer) index;
            }

            classHierarchy.put(clIndex,sclIndex);
        }
        qe.close();

        for (String s: classList.keySet()) {
            ttrClassTree.addItem(new Object[]{s}, (int) classList.get(s));
        }

        for (int i: classHierarchy.keySet()) {
            ttrClassTree.setParent(i,(int) classHierarchy.get(i));
        }

        for (int i=0; i<classList.size(); i++) {
            ttrClassTree.setCollapsed(i,false);
        }

    }



    private void initPropertyTree() {

        ttrPropertyTree.setSizeFull();
        ttrPropertyTree.setSelectable(true);

        ttrPropertyTree.addContainerProperty("Property", String.class, "");
        ttrPropertyTree.addContainerProperty("Domain", String.class, "");
        ttrPropertyTree.addContainerProperty("Range", String.class, "");
    }

    private void _populatePropertyTreeTable() {
        String getPropertyQuery = prefixes +
                "  SELECT ?property ?domain ?range ?subproperty WHERE {\n" +
                "    ?property  a ?p .\n" +
                "    FILTER (?p IN (rdf:Property, owl:AnnotationProperty, owl:DeprecatedProperty, owl:DatatypeProperty, owl:ObjectProperty, owl:FunctionalProperty, owl:InverseFunctionalProperty, owl:IrreflexiveProperty, owl:OntologyProperty, owl:ReflexiveProperty, owl:SymmetricProperty, owl:TransitiveProperty)) .\n" +
                "    OPTIONAL { ?property rdfs:subPropertyOf ?subproperty } .\n" +
                "    OPTIONAL { ?property rdfs:domain ?domain } .\n" +
                "    OPTIONAL { ?property rdfs:range ?range } .\n" +
                "  }";

        Query q = QueryFactory.create(getPropertyQuery);
        QueryExecution qe = com.hp.hpl.jena.query.QueryExecutionFactory.create(q, model);
        ResultSet results = qe.execSelect();


        HashMap<String,Integer> propertyList = new HashMap<String, Integer>();
        HashMap<Integer,Integer> propertyHierarchy = new HashMap<Integer, Integer>();
        //int []hierarchy = new int[results.getRowNumber()*2+1];
        int currentPropertyIndex = 0;

        while (results.hasNext()) {
            QuerySolution row= results.next();
            String prt= row.get("property").toString();
            String sprt= row.contains("subPropertyOf") ? row.get("subPropertyOf").toString() : "";
            String pdom= row.contains("domain") ? row.get("domain").toString() : "";
            String prng= row.contains("range") ? row.get("range").toString() : "";

            int clIndex, sclIndex;

            Object index = propertyList.get(prt);
            if (index == null ) {
                clIndex = currentPropertyIndex;
                propertyList.put(prt, currentPropertyIndex++);
            } else {
                clIndex = (Integer) index;
            }

            ttrPropertyTree.addItem(new Object[]{prt,pdom,prng}, (int) propertyList.get(prt));

            if ( ! sprt.equals("")) {
                index = propertyList.get(sprt);
                if (index == null ) {
                    sclIndex = currentPropertyIndex;
                    propertyList.put(sprt,currentPropertyIndex++);
                } else {
                    sclIndex = (Integer) index;
                }

                propertyHierarchy.put(clIndex, sclIndex);
            }


        }
        qe.close();

        for (int i: propertyHierarchy.keySet()) {
            ttrPropertyTree.setParent(i,(int) propertyHierarchy.get(i));
        }

        for (int i=0; i<propertyList.size(); i++) {
            ttrPropertyTree.setCollapsed(i,false);
        }

        /*for (int i=0; i< results.getRowNumber()*2; i++){
            if (hierarchy[i]!=-1)
                ttrClassTree.setParent(i,hierarchy[i]);
        } */


    }





    private void loadSchema(String uri){
        model.read(uri);

    }

}
