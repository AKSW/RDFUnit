package org.aksw.rdfunit.webdemo;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.aksw.rdfunit.webdemo.view.EndointTestTab;
import org.aksw.rdfunit.webdemo.view.IntroTab;

import javax.servlet.annotation.WebServlet;
/*
* @author Dimitris Kontokostas
*/

@SuppressWarnings("WeakerAccess")
@Theme("rdfunit")
@Push
public class RDFUnitDemo extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = RDFUnitDemo.class, widgetset = "org.aksw.rdfunit.webdemo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
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

        RDFUnitDemoSession.init();

        initLayout();

        mainTab.addTab(new IntroTab(), "Introduction");
        mainTab.addTab(new EndointTestTab(), "Test RDF Data");
        mainTab.setSelectedTab(1);

        // When user exits (window close, loose session) stop background threads
        this.addDetachListener(new DetachListener() {
            public void detach(DetachEvent event) {
                RDFUnitDemoSession.getTestExecutor().cancel();
                RDFUnitDemoSession.getTestGeneratorExecutor().cancel();
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

        Link rdfunit = new Link("",
                new ExternalResource("http://rdfunit.aksw.org/"));
        rdfunit.setIcon(new ThemeResource("images/logo-rdfunit.png"));

        headerLayout.addComponent(rdfunit);

        Link aksw = new Link("",
                new ExternalResource("http://aksw.org/"));
        aksw.setIcon(new ThemeResource("images/logo-aksw.png"));
        aksw.addStyleName("align-right");
        headerLayout.addComponent(aksw);
    }

    /*
    * Setup the footer of the page
    * */
    private void initLayoutFooter() {
        layoutFooter.setHeight("40px");
        layoutFooter.addStyleName("v-link");
        layoutFooter.addComponent(new Label("@ <a href=\"http://aksw.org\">AKSW</a> / <a href=\"http://rdfunit.aksw.org\">RDFUnit</a>", ContentMode.HTML));
    }

}
