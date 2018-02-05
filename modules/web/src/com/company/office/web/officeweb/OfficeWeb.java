package com.company.office.web.officeweb;

import com.company.office.web.screens.DialogScreen;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Component("office_OfficeWeb")
public class OfficeWeb {

    @Inject
    private ExportDisplay exportDisplay;

    public void showWarningMessage(Frame frame, String msg) {
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

    public void showFile(FileDescriptor file) {
        if (file == null)
            return;
        exportDisplay.show(file, ExportFormat.OCTET_STREAM);
    }

    public String showDailogScreen(Frame frame, String title, String info) {
        String res = null;

        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("info", info);
        DialogScreen dialogScreen = (DialogScreen) frame.openWindow("dialog-screen", WindowManager.OpenType.DIALOG, params);
        dialogScreen.addCloseWithCommitListener(() -> {
            showWarningMessage(frame, dialogScreen.getAnswer());
        });

        return null;
    }

}
