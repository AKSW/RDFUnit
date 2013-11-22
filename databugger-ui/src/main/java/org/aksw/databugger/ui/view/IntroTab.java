package org.aksw.databugger.ui.view;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

/**
 * User: Dimitris Kontokostas
 * displayes general information about the tool
 * Created: 11/15/13 7:41 PM
 */
public class IntroTab  extends VerticalLayout {

    public IntroTab() {
        initLayout();

    }

    private void initLayout() {
        this.setMargin(true);
        this.addComponent( new Label("<h2>Welcome to the Databugger Demo</h2>", ContentMode.HTML));

        this.addComponent( new Label(
                "<p>Please note that you cannot use all features of Databugger from this UI, for example, you cannot define your own tests cases or get your hands-on the generated test cases & results in RDF. " +
                        "For more thorough testing please try a local version of this UI and/or the command line (CLI) version.</p>" +
                "<p>Use the tab-sheets above to navigate or click on the buttons below to learn more about our tool.</p>",
                ContentMode.HTML));

        Link homepage = new Link("Homepage", new ExternalResource("http://databugger.aksw.org/"));
        Link github = new Link("Github page", new ExternalResource("https://github.com/AKSW/Databugger"));
        Link report = new Link("Report", new ExternalResource("http://svn.aksw.org/papers/2014/WWW_Databugger/public.pdf"));
        Link aksw = new Link("AKSW Entry", new ExternalResource("http://aksw.org/Projects/Databugger"));

        HorizontalLayout links = new HorizontalLayout();
        this.addComponent(links);
        links.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        links.addComponent(homepage);
        links.addComponent(new Label(" / "));
        links.addComponent(aksw);
        links.addComponent(new Label(" / "));
        links.addComponent(github);
        links.addComponent(new Label(" / "));
        links.addComponent(report);





    }
}
