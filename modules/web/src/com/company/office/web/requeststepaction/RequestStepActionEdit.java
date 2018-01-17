package com.company.office.web.requeststepaction;

import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
import com.company.office.web.officeeditor.OfficeEditor;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestStepActionEdit extends OfficeEditor<RequestStepAction> {

    @WindowParam
    private Request request;

    @WindowParam
    private List<RequestLog> logs;

    @Named("fieldGroup.type")
    private LookupField typeField;

    @Named("fieldGroup.message")
    private ResizableTextArea messageField;

    @Named("fieldGroup.description")
    private ResizableTextArea descriptionField;

    @Named("fieldGroupDates.submitted")
    private DateField submittedField;

    @Named("fieldGroupDates.approved")
    private DateField approvedField;

    @Inject
    private LookupField lookupTemplate;

    @Inject
    private FileUploadField uploadFile;

    @Inject
    private ExportDisplay exportDisplay;

    @Inject
    private ToolsService toolsService;

    @Inject
    private RequestService requestService;

    boolean closeFromExtraActions = false;

    @Override
    public void init(Map<String, Object> params) {
        addListeners();
        super.additional();
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (typeField.getValue() == null) {
            typeField.setValue(ActionType.sendFile);
        }
        setUserInterface();
        processActionType(typeField.getValue());
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        super.postCommit(committed, close);

        if (!closeFromExtraActions) {
            logs.add(requestService.newLogItem(request, null, makeName() + "edited", getItem()));
        }

        return true;
    }

    private void addListeners() {
        typeField.addValueChangeListener(e -> processActionType((ActionType) e.getValue()));
        lookupTemplate.addValueChangeListener(e -> setButtonParams());
    }

    private void setUserInterface() {
        uploadFile.setClearButtonCaption("");
        uploadFile.setUploadButtonCaption("");

        if (!toolsService.isAdmin()) {
            typeField.setEnabled(false);
            lookupTemplate.setEnabled(false);
            getComponentNN("fieldGroupDates").setEnabled(false);
        }

        switch (toolsService.getActiveGroupType()) {
            case Workers:
                getComponentNN("submitBtn").setVisible(false);
                getComponentNN("releaseBtn").setVisible(false);
                if (approvedField.getValue() == null) {
                    getComponentNN("rejectBtn").setVisible(false);
                    getComponentNN("approveBtn").setVisible(true);
                } else {
                    getComponentNN("rejectBtn").setVisible(true);
                    getComponentNN("approveBtn").setVisible(false);
                }
                break;
            case Applicants:
                descriptionField.setEnabled(false);

                if (submittedField.getValue() == null) {
                    String actionType = getItem().getType().getId();
                    boolean submitBtnVisible = false;

                    switch (actionType) {
                        case "file":
                            if (uploadFile.getValue() != null) {
                                submitBtnVisible = true;
                            }
                            break;
                        case "message":
                            if (messageField.getValue() != null) {
                                submitBtnVisible = true;
                            }
                            break;
                    }
                    getComponentNN("submitBtn").setVisible(submitBtnVisible);
                    getComponentNN("releaseBtn").setVisible(false);
                } else {
                    getComponentNN("submitBtn").setVisible(false);
                    getComponentNN("releaseBtn").setVisible(true);
                }
                getComponentNN("rejectBtn").setVisible(false);
                getComponentNN("approveBtn").setVisible(false);
                break;
            default:
        }
    }

    private void processActionType(ActionType type) {
        String actionType = type.getId();
        switch (actionType) {
            case "file":
                getComponentNN("boxFiles").setVisible(true);
                lookupTemplate.setRequired(true);
                uploadFile.setRequired(true);
                messageField.setVisible(false);
                messageField.setRequired(false);
                break;
            case "message":
                getComponentNN("boxFiles").setVisible(false);
                lookupTemplate.setRequired(false);
                uploadFile.setRequired(false);
                messageField.setVisible(true);
                messageField.setRequired(true);
                break;
        }
        setButtonParams();
    }

    private void setButtonParams() {
        getComponentNN("btnShowTemplate").setEnabled(getItem().getTemplate() != null);
    }

    public void onBtnShowTemplateClick() {
        if (getItem().getTemplate() == null)
            return;

        exportDisplay.show(getItem().getTemplate(), ExportFormat.OCTET_STREAM);
    }

    public void onReleaseBtnClick() {
        onExtraBtnClick("dialog.release", "released", submittedField, false);
    }

    public void onSubmitBtnClick() {
        onExtraBtnClick("dialog.submit", "submitted", submittedField, true);
    }

    public void onRejectBtnClick() {
        onExtraBtnClick("dialog.reject", "rejected", submittedField, false);
    }

    public void onApproveBtnClick() {
        onExtraBtnClick("dialog.approve", "approved", approvedField, true);
    }

    private String makeName() {
        return String.format(getMessage("action.name"), getItem().getDescription());
    }

    private void onExtraBtnClick(String msg, String info, DateField field, boolean setValue) {
        showOptionDialog(
                "",
                getMessage(msg),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            field.setValue(setValue ? new Date() : null);
                            logs.add(requestService.newLogItem(request, null, makeName() + info, getItem()));
                            closeFromExtraActions = true;
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }
}