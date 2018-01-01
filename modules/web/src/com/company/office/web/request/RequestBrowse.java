package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.ActionType;
import com.company.office.entity.RequestAction;
import com.company.office.service.ToolsService;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.Request;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class RequestBrowse extends EntityCombinedScreen {

    @Inject
    private ToolsService toolsService;

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> applicantDs;

    @Inject
    private CollectionDatasource<User, UUID> workerDs;

    @Inject
    private CollectionDatasource<RequestAction, UUID> actionsDs;

    @Named("fieldGroup.applicant")
    private PickerField applicantField;

    @Named("fieldsAction.file")
    private FileUploadField fileField;

    @Named("fieldsAction.message")
    private ResizableTextArea messageField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        /*
        if (officeConfig.getApplicantsGroup() != null) {
            applicantDs.setQuery(officeConfig.getApplicantsGroupQuery());
        }
        */
        applicantField.getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            workerDs.setQuery(officeConfig.getWorkersGroupQuery());
        }

        actionsDs.addItemChangeListener(e -> {
            if (actionsDs.getItem() != null) {
                ActionType actionType = actionsDs.getItem().getType();

                fileField.setVisible(actionType.equals(ActionType.sendFile));
                messageField.setVisible(actionType.equals(ActionType.sendMessage));
            }
        });

        if (!toolsService.isSuperUser()) {
            ((TabSheet) getComponentNN("tabSheet")).getTab("tabSystem").setVisible(false);
        }

        if (toolsService.getGroup().equals(officeConfig.getRegistratorsGroup())) {
            ((TabSheet) getComponentNN("tabSheet")).getTab("stepsTab").setVisible(false);
            ((TabSheet) getComponentNN("tabSheet")).getTab("actionsTab").setVisible(false);
            ((TabSheet) getComponentNN("tabSheet")).getTab("communicationsTab").setVisible(false);
        }

    }

    public Component snGenerator(Request request) {
        String res = "";
        if (request.getSeries() != null) {
            res += request.getSeries() + "-";
        }

        if (request.getNumber() != null) {
            res += request.getNumber();
        }

        return new Table.PlainTextCell(res);
    }

    public void saveWithPrompt() {
        showOptionDialog(
                messages.getMainMessage("dialog.saveAndClose.title"),
                messages.getMainMessage("dialog.saveAndClose.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            super.save();
                            getTable().getDatasource().refresh();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

}