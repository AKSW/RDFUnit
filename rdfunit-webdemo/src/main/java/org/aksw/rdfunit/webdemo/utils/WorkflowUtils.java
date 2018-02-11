package org.aksw.rdfunit.webdemo.utils;

import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.aksw.rdfunit.webdemo.view.WorkflowItem;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/4/14 7:21 PM

 */
public final class WorkflowUtils {

    public static boolean checkIfPreviousItemIsReady(WorkflowItem item) {
        WorkflowItem p = item.getPreviousItem();
        if (p == null)
            return true;

        return p.isReady();
    }


    public static void setMessage(final Label label, final String message, final boolean isError) {
        UI.getCurrent().access(() -> {
            if (isError) {
                label.setStyleName(ValoTheme.LABEL_FAILURE);
            } else {
                label.setStyleName(ValoTheme.LABEL_SUCCESS);
            }

            label.setValue(message);
            CommonAccessUtils.pushToClient();
        });

    }
}
