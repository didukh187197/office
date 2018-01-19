package com.company.office.web.requeststepaction;

import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
import com.company.office.web.officeeditor.OfficeEditor;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;

public class RequestStepActionEdit extends OfficeEditor<RequestStepAction> {

    @WindowParam
    private Request request;

    @Inject
    private ToolsService toolsService;

    @Inject
    private RequestService requestService;

    @Inject
    private ExportDisplay exportDisplay;

    @Inject
    private LookupField lookupTemplate;

    @Inject
    private FileUploadField uploadFile;

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
            request.getLogs().add(
                    requestService.newLogItem(request, null, makeName() + " edited", getItem())
            );
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
                getComponentNN("rejectBtn").setVisible(false);
                getComponentNN("approveBtn").setVisible(false);

                if (submittedField.getValue() == null) {
                    getComponentNN("submitBtn").setVisible(true);
                    getComponentNN("releaseBtn").setVisible(false);
                } else {
                    messageField.setEnabled(false);
                    uploadFile.setEnabled(false);
                    getComponentNN("okBtn").setEnabled(false);
                    getComponentNN("submitBtn").setVisible(false);
                    getComponentNN("releaseBtn").setVisible(true);
                }
                break;
            default:
                getComponentNN("submitBtn").setVisible(false);
                getComponentNN("releaseBtn").setVisible(false);
                getComponentNN("rejectBtn").setVisible(false);
                getComponentNN("approveBtn").setVisible(false);
                submittedField.setEnabled(true);
                approvedField.setEnabled(true);
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

    private void showFile(FileDescriptor file) {
        if (file == null)
            return;

        exportDisplay.show(file, ExportFormat.OCTET_STREAM);
    }

    public void onBtnShowTemplateClick() {
        showFile(getItem().getTemplate());
    }

    public void onBtnShowFileClick() {
        showFile(getItem().getFile());
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
                            request.getLogs().add(
                                    requestService.newLogItem(request, null, makeName() + " " + info, getItem())
                            );
                            closeFromExtraActions = true;
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

}