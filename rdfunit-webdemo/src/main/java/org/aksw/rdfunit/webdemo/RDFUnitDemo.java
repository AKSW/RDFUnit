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
import org.aksw.rdfunit.webdemo.view.EndpointTestTab;
import org.aksw.rdfunit.webdemo.view.IntroTab;

import javax.servlet.annotation.WebServlet;
/*
* @author Dimitris Kontokostas
*/

/**
 * The main Vaadin UI entry Class
 *
 * @author jim
 * @version $Id: $Id
 */
@SuppressWarnings("WeakerAccess")
@Theme("rdfunit")
@Push
public class RDFUnitDemo extends UI {

    /**
     * Setup the servlet
     */
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
    /** {@inheritDoc} */
    protected void init(VaadinRequest request) {


        RDFUnitDemoSession.init("http://" + getClientIpAddr(request));

        initLayout();
        setPollInterval(15000);

        mainTab.addTab(new IntroTab(), "Introduction");
        mainTab.addTab(new EndpointTestTab(), "Test RDF Data");
        mainTab.setSelectedTab(1);

        // When user exits (window close, loose session) stop background threads
        this.addDetachListener((DetachListener) event -> {
            RDFUnitDemoSession.getTestExecutor().cancel();
            RDFUnitDemoSession.getTestGeneratorExecutor().cancel();
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

    private String getClientIpAddr(VaadinRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
