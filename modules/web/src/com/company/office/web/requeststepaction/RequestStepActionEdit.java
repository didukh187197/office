package com.company.office.web.requeststepaction;

import com.company.office.entity.*;
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

    @Inject
    private ToolsService toolsService;

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

    @Override
    public void init(Map<String, Object> params) {
        addListeners();
        super.additional();
    }

    @Override
    protected void postInit() {
        if (typeField.getValue() == null) {
            typeField.setValue(ActionType.sendFile);
        }
        setUserInterface();
        processActionType(typeField.getValue());
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        super.postCommit(committed, close);

        RequestStepAction requestStepAction = (RequestStepAction) ((Editor) frame).getItem();
        Request request = requestStepAction.getRequestStep().getRequest();
        RequestLog requestLog = new RequestLog();
        requestLog.setRequest(request);
        requestLog.setInfo(toolsService.getActiveUser().getName() + "performs action: " + getItem().getDescription());
        logs.add(requestLog);

        return true;
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
                    getComponentNN("submitBtn").setVisible(true);
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

    private void addListeners() {
        typeField.addValueChangeListener(e -> processActionType((ActionType) e.getValue()));
        lookupTemplate.addValueChangeListener(e -> setButtonParams());
    }

    private void processActionType(ActionType type) {
        String actionType = type.getId();

        getComponentNN("boxFiles").setVisible(actionType.equals("file"));
        lookupTemplate.setRequired(actionType.equals("file"));
        //uploadFile.setRequired(actionType.equals("file"));
        messageField.setVisible(actionType.equals("message"));
        messageField.setRequired(actionType.equals("message"));

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
        onExtraBtnClick("dialog.release", submittedField, false);
    }

    public void onSubmitBtnClick() {
        onExtraBtnClick("dialog.submit", submittedField, true);
    }

    public void onRejectBtnClick() {
        onExtraBtnClick("dialog.reject", submittedField, false);
    }

    public void onApproveBtnClick() {
        onExtraBtnClick("dialog.approve", approvedField, true);
    }

    private void onExtraBtnClick(String msg, DateField field, boolean setValue) {
        showOptionDialog(
                "",
                getMessage(msg),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            field.setValue(setValue ? new Date() : null);
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }
}