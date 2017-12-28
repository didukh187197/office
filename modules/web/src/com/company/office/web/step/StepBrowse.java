package com.company.office.web.step;

import com.company.office.entity.ActionType;
import com.company.office.entity.Step;
import com.company.office.entity.StepAction;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class StepBrowse extends EntityCombinedScreen {

    @Inject
    private CollectionDatasource<StepAction, UUID> actionsDs;

    @Named("fieldsAction.template")
    private FileUploadField templateField;

    @Inject
    protected Messages messages;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        actionsDs.addItemChangeListener(e -> {
            if (actionsDs.getItem() != null) {
                ActionType actionType = actionsDs.getItem().getType();
                templateField.setVisible(actionType.equals(ActionType.sendFile));
            }
        });
    }

    public void saveWithPrompt() {
        showOptionDialog(
                messages.getMainMessage("dialog.saveAndClose.title"),
                messages.getMainMessage("dialog.saveAndClose.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            String res = "";

                            Step step = (Step) getFieldGroup().getDatasource().getItem();
                            if ((step.getActions() == null) || (step.getActions().size() == 0)) {
                                res += getMessage("list.actions");
                            }

                            if ((step.getUsers() == null) || (step.getUsers().size() == 0)) {
                                if (res.length() != 0) {
                                    res += "\n";
                                }
                                res += getMessage("list.users");
                            }

                            if (res.length() != 0) {
                                showNotification(messages.getMainMessage("warning.emptyData"), res, NotificationType.WARNING);
                            }

                            super.save();
                            CollectionDatasource browseDs = getTable().getDatasource();
                            browseDs.refresh();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }
}