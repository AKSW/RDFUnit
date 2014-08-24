package org.aksw.rdfunit.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.rdfunit.ui.view.EndointTestTab;
import org.aksw.rdfunit.ui.view.IntroTab;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.annotation.WebServlet;
/*
* @author Dimitris Kontokostas
*/

@Theme("rdfunit")
public class RDFUnitUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = RDFUnitUI.class, widgetset = "org.aksw.rdfunit.ui.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    static {
        SLF4JBridgeHandler.install();
    }
        /* User interface components are stored in session. */

    private final HorizontalLayout headerLayout = new HorizontalLayout();
    private final TabSheet mainTab = new TabSheet();
    private final HorizontalLayout layoutFooter = new HorizontalLayout();

    /*
     * After UI class is created, init() is executed. You should build and wire
     * up your user interface here.
     */
    protected void init(VaadinRequest request) {

        RDFUnitUISession.init();

        initLayout();

        mainTab.addTab(new IntroTab(), "Welcome");
        mainTab.addTab(new EndointTestTab(), "Test an Endpoint");
        //mainTab.setSelectedTab(1);

        // When user exits (window close, loose session) stop background threads
        this.addDetachListener(new DetachListener() {
            public void detach(DetachEvent event) {
                RDFUnitUISession.getTestExecutor().cancel();
                RDFUnitUISession.getTestGeneratorExecutor().cancel();
            }
        });

    }

    /*
     * In this example layouts are programmed in Java. You may choose use a
     * visual editor, CSS or HTML templates for layout instead.
     */
    private void initLayout() {

        VerticalLayout page = new VerticalLayout();
        setContent(page);
        page.setId("page");

        page.addComponent(headerLayout);
        initLayoutHeader();

        page.addComponent(mainTab);
        initLayoutMain();

        initLayoutFooter();
        page.addComponent(layoutFooter);

    }

    /*
    * setup the header of the page
    * */
    private void initLayoutHeader() {
        headerLayout.setWidth("100%");
        headerLayout.setHeight("80px");
        headerLayout.setId("header");

        VerticalLayout rdfunitLogo = new VerticalLayout();
        rdfunitLogo.setStyleName("logo");
        rdfunitLogo.addComponent(new Label("<h1>RDFUnit</h1>", ContentMode.HTML));
        rdfunitLogo.addComponent(new Label("<span>A Data Debugging Framework</span>", ContentMode.HTML));


        headerLayout.addComponent(rdfunitLogo);

        Link aksw = new Link("",
                new ExternalResource("http://aksw.org/"));
        aksw.setIcon(new ThemeResource("images/logo-aksw.png"));
        aksw.addStyleName("align-right");
        headerLayout.addComponent(aksw);
    }

    /*
    * setup the main content of the page
    * */
    private void initLayoutMain() {
    }

    /*
    * Setup the footer of the page
    * */
    private void initLayoutFooter() {
        layoutFooter.setHeight("40px");
        layoutFooter.addStyleName("v-link");
        layoutFooter.addComponent(new Label("@ <a href=\"http://aksw.org\">AKSW</a> / <a href=\"http://aksw.org/Projects/RDFUnit\">RDFUnit</a>", ContentMode.HTML));
    }

}
