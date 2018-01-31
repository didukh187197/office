package com.company.office.web.officeweb;

import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.Table;
import org.springframework.stereotype.Component;


@Component("office_OfficeWeb")
public class OfficeWeb {

    public void showWarningMessage(Frame frame, String msg) {
        //showMessageDialog("", msg, MessageType.CONFIRMATION);
        frame.showNotification(msg, Frame.NotificationType.WARNING);
    }

    public void showErrorMessage(Frame frame, String msg) {
        //showMessageDialog("", msg, MessageType.CONFIRMATION);
        frame.showNotification(msg, Frame.NotificationType.ERROR);
    }

    public void disableContainer(Frame frame, String containerID) {
        com.haulmont.cuba.gui.components.Component.Container container = (com.haulmont.cuba.gui.components.Component.Container) frame.getComponentNN(containerID);
        boolean enabled = false;
        ComponentsHelper.walkComponents(container, (component, name) -> {
            if (component instanceof FieldGroup) {
                ((FieldGroup) component).setEditable(enabled);
            } else if (component instanceof Table) {
                ((Table) component).getActions().forEach(action -> action.setEnabled(enabled));
            } else if (!(component instanceof com.haulmont.cuba.gui.components.Component.Container)) {
                component.setEnabled(enabled);
            }
        });
    }

}
