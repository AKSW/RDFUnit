package org.aksw.rdfunit.ui.view;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

/**
 * @author Dimitris Kontokostas
 *         displayes general information about the tool
 * @since 11/15/13 7:41 PM
 */
public class IntroTab extends VerticalLayout {

    public IntroTab() {
        initLayout();

    }

    private void initLayout() {
        this.setMargin(true);
        this.addComponent(new Label("<h2>RDFUnit - A Data Debugging Framework for the Web of Data</h2>", ContentMode.HTML));

        this.addComponent(new Label(
                "<p><strong>Welcome to the RDFUnit Demo</strong></p>" +
                        "<p>RDFUnit is a testing framework that can verify your data against a schema / vocabulary or custom SPARQL test cases.</p>" +
                        "<p>Select the <strong>Test an Endpoint</strong> option to from the menu to test your endpoint against a set of schemas or try one of our predefined examples. " +
                        "We make use of SPARQL 1.1 property paths thus, an endpoint with such functionality (i.e. Virtuoso 7) is recommended.</p>" +
                        "<p>Please note that you cannot use all features of RDFUnit from this UI. For example, you cannot define your own tests cases or get your hands-on the generated test cases & results in RDF. " +
                        "For more thorough testing please try a local version of this UI and/or the command line (CLI) version.</p>",
                ContentMode.HTML));

        Link homepage = new Link("Homepage", new ExternalResource("http://rdfunit.aksw.org/"));
        Link github = new Link("Github page", new ExternalResource("https://github.com/AKSW/RDFUnit"));
        Link report = new Link("Report", new ExternalResource("http://svn.aksw.org/papers/2014/WWW_Databugger/public.pdf"));
        Link aksw = new Link("AKSW Entry", new ExternalResource("http://aksw.org/Projects/RDFUnit"));

        HorizontalLayout links = new HorizontalLayout();
        this.addComponent(links);
        links.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        links.addComponent(new Label("To learn more about RDFUnit you can navigate to our: &nbsp;", ContentMode.HTML));
        links.addComponent(homepage);
        links.addComponent(new Label(" / "));
        links.addComponent(aksw);
        links.addComponent(new Label(" / "));
        links.addComponent(github);
        links.addComponent(new Label(" / "));
        links.addComponent(report);


    }
}
