package org.aksw.rdfunit.webdemo.view;

import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.tokenfield.TokenField;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Dimitris Kontokostas
 *         provides a schema
 * @since 11/18/13 11:26 AM
 */
final class SchemaSelectorComponent extends VerticalLayout {
    private static final Logger log = LoggerFactory.getLogger(SchemaSelectorComponent.class);

    final private TokenField tokenField;

    public SchemaSelectorComponent() {

        VerticalLayout p = new VerticalLayout();
        //p.getContent().setStyleName("black");
        addComponent(p);

        //Label
        p.addComponent(new Label("Test against the following schemas"));

        // generate container
        Container tokens = generateTestContainer();

        VerticalLayout lo = new VerticalLayout();
        lo.setSpacing(true);

        //Taken/adapted from Vaadin:TokenField example
        tokenField = new TokenField(lo) {

            // dialog if not in 'address book', otherwise just add
            protected void onTokenInput(Object tokenId) {
                Set<Object> set = (Set<Object>) getValue();
                SchemaSource s = null;
                if (tokenId != null) {
                    if (tokenId instanceof SchemaSource) {
                        s = new SchemaSource((SchemaSource) tokenId);
                    }
                    if (tokenId instanceof String) {
                        s = new SchemaSource(SchemaService.getSourceFromPrefix(RDFUnitDemoSession.getBaseDir(), (String) tokenId));
                    }
                }


                if (set != null && set.contains(s)) {
                    // duplicate
                    Notification.show(getTokenCaption(tokenId)
                            + " already exists");
                } else {
                    if (s == null || !cb.containsId(s)) {
                        // don't add directly,
                        // show custom "add to address book" dialog
                        UI.getCurrent().addWindow(new EditContactWindow(
                                tokenId != null ? tokenId.toString() : "", this));
                    } else {
                        // it's in the 'address book', just add
                        addToken(new SchemaSource(s));
                    }
                }
            }

            // show confirm dialog
            protected void onTokenClick(final Object tokenId) {
                UI.getCurrent().addWindow(
                        new RemoveWindow((SchemaSource) tokenId, this));
            }

            // just delete, no confirm
            protected void onTokenDelete(Object tokenId) {
                this.removeToken(tokenId);
            }

            // custom caption + style if not in 'address book'
            protected void configureTokenButton(Object tokenId,
                                                Button button) {
                super.configureTokenButton(tokenId, button);
                // custom caption
                button.setCaption(tokenId.toString());
                // width
                button.setWidth("100%");

                if (!cb.containsId(tokenId)) {
                    // it's not in the address book; style
                    button
                            .addStyleName(TokenField.STYLE_BUTTON_EMPHAZISED);
                }
            }
        };
        p.addComponent(tokenField);
        // This would turn on the "fake tekstfield" look:
        tokenField.setStyleName(TokenField.STYLE_TOKENFIELD);
        tokenField.setWidth("100%");
        tokenField.setInputWidth("100%");
        tokenField.setContainerDataSource(tokens); // 'address book'
        tokenField.setFilteringMode(FilteringMode.CONTAINS); // suggest
        tokenField.setInputPrompt("Enter prefix or URI");
        tokenField.setRememberNewTokens(false); // we'll do this via the dialog
        tokenField.setImmediate(true);
    }


    /**
     * This is the window used to add new contacts to the 'address book'. It
     * does not do proper validation - you can add weird stuff.
     */
    public static class EditContactWindow extends Window {
        private String prefix = "";
        private String uri = "";

        EditContactWindow(final String t, final TokenField f) {
            super("New Schema");
            VerticalLayout l = new VerticalLayout();
            setContent(l);
            if (t.contains("http://")) {
                uri = t;
            } else {
                prefix = t;
            }
            setModal(true);
            center();
            setWidth("250px");
            setStyleName("black");
            setResizable(false);

            // Have some layout and create the fields
            PropertysetItem item = new PropertysetItem();
            item.addItemProperty("Prefix", new ObjectProperty<>(prefix));
            item.addItemProperty("URI", new ObjectProperty<>(uri));

            final FormLayout form = new FormLayout();
            form.setMargin(true);
            final TextField prefixField = new TextField("Prefix", prefix);
            form.addComponent(prefixField);
            final TextField uriField = new TextField("URI", uri);
            form.addComponent(uriField);

            FieldGroup binder = new FieldGroup(item);
            binder.bind(prefixField, "Prefix");
            binder.bind(uriField, "URI");

            l.addComponent(form);

            // layout buttons horizontally
            HorizontalLayout hz = new HorizontalLayout();
            l.addComponent(hz);
            hz.setSpacing(true);
            hz.setWidth("100%");

            Button cancel = new Button("Cancel", new Button.ClickListener() {

                private static final long serialVersionUID = -1198191849568844582L;

                public void buttonClick(ClickEvent event) {
                    close();
                }
            });
            hz.addComponent(cancel);
            hz.setComponentAlignment(cancel, Alignment.MIDDLE_LEFT);

            Button add = new Button("Add to list",
                    new Button.ClickListener() {

                        private static final long serialVersionUID = 1L;

                        public void buttonClick(ClickEvent event) {
                            prefix = prefixField.getValue();
                            uri = uriField.getValue();
                            if (!(prefix == null || uri == null || prefix.isEmpty() || uri.isEmpty())) {
                                SchemaSource source = new SchemaSource(prefix, uri, RDFReaderFactory.createDereferenceReader(uri));
                                ((BeanItemContainer) f.getContainerDataSource())
                                        .addBean(source);
                                f.addToken(source);
                            } else {
                                Notification.show("Invalid source for prefix: \"" + prefix + "\" and URI: " + uri + "\"");
                            }
                            close();


                        }
                    });
            hz.addComponent(add);
            hz.setComponentAlignment(add, Alignment.MIDDLE_RIGHT);

        }
    }

    private static Container generateTestContainer() {
        BeanItemContainer<SchemaSource> container = new BeanItemContainer<>(
                SchemaSource.class);

        java.util.Collection<SchemaSource> sources;
        try {
            sources = SchemaService.getSourceListAll(false, null);
        } catch (UndefinedSchemaException e) {
            log.error("Undefined schema");
            sources = new ArrayList<>();
        }
        //Collections.sort(sources);

        for (SchemaSource s : sources)
            container.addBean(s);
        return container;
    }


    /**
     * This is the window used to confirm removal
     */
    public static class RemoveWindow extends Window {

        private static final long serialVersionUID = -7140907025722511460L;

        RemoveWindow(final Source s, final TokenField f) {
            super("Remove " + s.getPrefix() + "?");
            FormLayout outer = new FormLayout();
            outer.setSpacing(true);
            setContent(outer);

            VerticalLayout l = new VerticalLayout();
            l.setSpacing(true);
            outer.addComponent(l);


            setStyleName("black");
            setResizable(false);
            center();
            setModal(true);
            setWidth("320px");
            setHeight("170px");
            setClosable(true);

            l.addComponent(new Label("Are you sure you want to remove " + s.getPrefix() + "?\n" + s.getUri()));

            // layout buttons horizontally
            HorizontalLayout hz = new HorizontalLayout();
            l.addComponent(hz);
            hz.setSpacing(true);
            //hz.setWidth("100%");

            Button cancel = new Button("Cancel", new Button.ClickListener() {

                private static final long serialVersionUID = 7675170261217815011L;

                public void buttonClick(ClickEvent event) {
                    RemoveWindow.this.close();
                }
            });
            hz.addComponent(cancel);
            hz.setComponentAlignment(cancel, Alignment.MIDDLE_LEFT);

            Button remove = new Button("Remove", new Button.ClickListener() {

                private static final long serialVersionUID = 5004855711589989635L;

                public void buttonClick(ClickEvent event) {
                    f.removeToken(s);
                    RemoveWindow.this.close();
                }
            });
            hz.addComponent(remove);
            hz.setComponentAlignment(remove, Alignment.MIDDLE_RIGHT);

        }
    }

    public java.util.Collection<SchemaSource> getSelections() {
        java.util.Collection<SchemaSource> sources = new ArrayList<>();

        Object selectedSources = tokenField.getValue();

        if (selectedSources instanceof Set) {
            for (Object o : (Set) selectedSources)
                sources.add((SchemaSource) o);
        } else
            sources.add((SchemaSource) selectedSources);

        return sources;
    }

    public void setSelections(java.util.Collection<SchemaSource> sources) {
        clearSelections();
        for (SchemaSource s : sources)
            tokenField.addToken(s);
    }

    private void clearSelections() {
        tokenField.setValue(new LinkedHashSet<SchemaSource>());

    }

}
