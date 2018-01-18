package com.company.office.web.officeweb;

import com.haulmont.cuba.gui.components.Frame;
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

}
