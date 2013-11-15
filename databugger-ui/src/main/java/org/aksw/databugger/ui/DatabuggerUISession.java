package org.aksw.databugger.ui;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

/**
 * User: Dimitris Kontokostas
 * Keeps user session variables
 * Created: 11/15/13 9:52 AM
 */
public class DatabuggerUISession extends VaadinSession {

    public DatabuggerUISession(VaadinService service) {
        super(service);
    }
}
