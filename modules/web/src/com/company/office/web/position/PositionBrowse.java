package com.company.office.web.position;

import com.company.office.entity.ActionType;
import com.company.office.common.OfficeTools;
import com.company.office.entity.Position;
import com.company.office.entity.PositionAction;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PositionBrowse extends EntityCombinedScreen {

    @Inject
    private OfficeTools officeTools;

    @Inject
    private CollectionDatasource<PositionAction, UUID> actionsDs;

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

        if (!officeTools.isAdmin()) {
            ((TabSheet) getComponentNN("tabSheet")).getTab("tabSystem").setVisible(false);
        }
    }

    public void saveWithPrompt() {
        showOptionDialog(
                messages.getMainMessage("dialog.saveAndClose.title"),
                messages.getMainMessage("dialog.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            String res = "";

                            Position position = (Position) getFieldGroup().getDatasource().getItem();
                            if ((position.getActions() == null) || (position.getActions().size() == 0)) {
                                res += getMessage("list.actions");
                            }

                            if ((position.getUsers() == null) || (position.getUsers().size() == 0)) {
                                if (res.length() != 0) {
                                    res += "\n";
                                }
                                res += getMessage("list.users");
                            }

                            if (res.length() != 0) {
                                showNotification(messages.getMainMessage("warning.emptyData"), res, NotificationType.WARNING);
                            }

                            super.save();
                            getTable().getDatasource().refresh();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }
}