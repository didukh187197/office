package com.company.office.web.officeweb;

import com.company.office.entity.RequestStep;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("office_OfficeWeb")
public class OfficeWeb {

    @Inject
    private ExportDisplay exportDisplay;

    public void showWarningMessage(Frame frame, String msg) {
        frame.showNotification(msg, Frame.NotificationType.WARNING);
    }

    public void showErrorMessage(Frame frame, String msg) {
        frame.showNotification(msg, Frame.NotificationType.ERROR);
    }

    public void disableContainer(Frame frame, String containerID) {
        com.haulmont.cuba.gui.components.Component.Container container = (com.haulmont.cuba.gui.components.Component.Container) frame.getComponentNN(containerID);
        ComponentsHelper.walkComponents(container, (component, name) -> {
            if (component instanceof FieldGroup) {
                ((FieldGroup) component).setEditable(false);
            } else if (component instanceof Table) {
                ((Table) component).getActions().forEach(action -> action.setEnabled(false));
            } else if (!(component instanceof com.haulmont.cuba.gui.components.Component.Container)) {
                component.setEnabled(false);
            }
        });
    }

    public void showFile(FileDescriptor file) {
        if (file == null)
            return;
        exportDisplay.show(file, ExportFormat.OCTET_STREAM);
    }

    public void showStep(Frame frame, RequestStep step) {
        frame.openEditor("office$RequestStep.edit", step, WindowManager.OpenType.DIALOG);
    }

}
