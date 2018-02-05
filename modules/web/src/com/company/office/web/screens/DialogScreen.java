package com.company.office.web.screens;

import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.util.Map;

public class DialogScreen extends AbstractWindow {

    @Inject
    private Label infoLabel;

    @Inject
    private ResizableTextArea answerText;

    public String getAnswer() {
        return answerText.getValue();
    }

    @Override
    public void init(Map<String, Object> params) {
        setCaption((String) params.get("title"));
        infoLabel.setValue(
                String.format(getMessage("dialogScreen.prompt"), params.get("actionId"))
        );
        infoLabel.setStyleName("dialog-prompt");
    }

    public void onOkBtnClick() {
        if (answerText.getValue() == null) {
            showNotification(getMessage("dialogScreen.empty"), NotificationType.ERROR);
            return;
        }

        showOptionDialog(
                messages.getMainMessage("dialog.acceptAndClose.title"),
                messages.getMainMessage("dialog.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            this.close(Window.COMMIT_ACTION_ID);
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onCloseBtnClick() {
        this.close(Window.CLOSE_ACTION_ID);
    }

}