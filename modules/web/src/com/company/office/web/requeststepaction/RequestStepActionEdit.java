package com.company.office.web.requeststepaction;

import com.company.office.entity.*;
import com.company.office.common.OfficeCommon;
import com.company.office.common.OfficeTools;
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
    private OfficeCommon officeCommon;

    @Inject
    private OfficeTools officeTools;

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
                    officeCommon.newLogItem(request, null, makeName() + " edited", getItem())
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

        if (!officeTools.isAdmin()) {
            typeField.setEnabled(false);
            lookupTemplate.setEnabled(false);
            getComponentNN("fieldGroupDates").setEnabled(false);
            submittedField.setEnabled(true);
            approvedField.setEnabled(true);
        }

        Component submitBtn = getComponentNN("submitBtn");
        Component releaseBtn = getComponentNN("releaseBtn");
        Component rejectBtn = getComponentNN("rejectBtn");
        Component approveBtn = getComponentNN("approveBtn");
        Component disapproveBtn = getComponentNN("disapproveBtn");

        switch (officeTools.getActiveGroupType()) {
            case Workers:
                messageField.setEnabled(false);
                uploadFile.setEnabled(false);

                if (submittedField.getValue() == null)
                    return;

                getComponentNN("workerButtons").setVisible(true);

                if (approvedField.getValue() == null) {
                    rejectBtn.setVisible(true);
                    approveBtn.setVisible(true);
                    disapproveBtn.setVisible(false);
                } else {
                    rejectBtn.setVisible(false);
                    approveBtn.setVisible(false);
                    disapproveBtn.setVisible(true);
                }
                break;
            case Applicants:
                descriptionField.setEnabled(false);
                getComponentNN("applicantButtons").setVisible(true);

                if (submittedField.getValue() == null) {
                    submitBtn.setVisible(requiredDataSet());
                    releaseBtn.setVisible(false);
                } else {
                    messageField.setEnabled(false);
                    uploadFile.setEnabled(false);
                    getComponentNN("okBtn").setEnabled(false);
                    submitBtn.setVisible(false);
                    releaseBtn.setVisible(true);
                }
                break;
        }
    }

    private boolean requiredDataSet() {
        boolean res = true;
        switch ((ActionType) typeField.getValue()) {
            case sendFile:
                if (uploadFile.getValue() == null)
                    res = false;
                break;
            case sendMessage:
                if (messageField.getValue() == null)
                    res = false;
                break;
            default:
        }
        return res;
    }

    private void processActionType(ActionType type) {
        switch (type) {
            case sendFile:
                getComponentNN("boxFiles").setVisible(true);
                lookupTemplate.setRequired(true);
                uploadFile.setRequired(true);
                messageField.setVisible(false);
                messageField.setRequired(false);
                break;
            case sendMessage:
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
        if (!requiredDataSet())
            return;

        onExtraBtnClick("dialog.submit", "submitted", submittedField, true);
    }

    public void onRejectBtnClick() {
        onExtraBtnClick("dialog.reject", "rejected", submittedField, false);
    }

    public void onApproveBtnClick() {
        onExtraBtnClick("dialog.approve", "approved", approvedField, true);
    }

    public void onDisapproveBtnClick() {
        onExtraBtnClick("dialog.disapprove", "disapproved", approvedField, false);
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
                                    officeCommon.newLogItem(request, null, makeName() + " " + info, getItem())
                            );
                            closeFromExtraActions = true;
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }



}