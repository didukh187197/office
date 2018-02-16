package com.company.office.web.officeweb;

import com.company.office.entity.RequestStep;
import com.company.office.entity.RequestStepAction;
import com.company.office.service.ShedulerService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.App;
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

    //Admin methods
    // Used from main menu
    private final String MSG_PACK = "com.company.office.web.officeweb";

    @Inject
    private Messages messages;

    @Inject
    private ShedulerService shedulerService;

    @SuppressWarnings("unused")
    public void checkProcessingDelay() {
        shedulerService.checkProcessingDelay();
        showWarningMessage(App.getInstance().getTopLevelWindow().getFrame(), messages.getMessage(MSG_PACK, "checkRequests"));
    }

    @SuppressWarnings("unused")
    public void setPositionUser() {
        shedulerService.setPositionUser();
        showWarningMessage(App.getInstance().getTopLevelWindow().getFrame(), messages.getMessage(MSG_PACK, "checkRequests"));
    }

    @Inject
    private ComponentsFactory componentsFactory;

    public Label getMarkForGenerator(RequestStepAction requestStepAction) {
        Label lbl = componentsFactory.createComponent(Label.class);
        lbl.setValue("");
        switch (requestStepAction.getType()) {
            case sendFile:
                lbl.setIconFromSet(requestStepAction.getFile() != null ? CubaIcon.CHECK_SQUARE_O : CubaIcon.SQUARE_O);
                break;
            case sendMessage:
                lbl.setIconFromSet(requestStepAction.getMessage() != null ? CubaIcon.CHECK_SQUARE_O : CubaIcon.SQUARE_O);
                break;
        }
        return lbl;
    }

}
