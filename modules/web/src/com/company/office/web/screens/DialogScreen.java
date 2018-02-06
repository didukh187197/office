package com.company.office.web.screens;

import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.util.Map;

public class DialogScreen extends AbstractWindow {

    @Inject
    private Label infoLabel;

    @Inject
    private ResizableTextArea reasonText;

    public String getReason() {
        return reasonText.getValue();
    }

    @Override
    public void init(Map<String, Object> params) {
        String actionId = (String) params.get("actionId");
        String actionName = getMessage("dialogScreen." + actionId);

        String title = actionName.substring(0, 1).toUpperCase() + actionName.substring(1) + " " + getMessage("dialogScreen.request");
        setCaption(title);

        infoLabel.setValue(
                String.format(getMessage("dialogScreen.prompt"), actionName)
        );
        infoLabel.setStyleName("dialog-prompt");
    }

    public void onOkBtnClick() {
        if (reasonText.getValue() == null) {
            showNotification(getMessage("dialogScreen.empty"), NotificationType.ERROR);
            return;
        }

        showOptionDialog(
                messages.getMainMessage("dialog.acceptAndClose.title"),
                messages.getMainMessage("dialog.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> this.close(Window.COMMIT_ACTION_ID)),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onCloseBtnClick() {
        this.close(Window.CLOSE_ACTION_ID);
    }

}