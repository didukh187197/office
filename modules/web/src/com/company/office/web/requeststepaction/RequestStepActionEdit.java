package com.company.office.web.requeststepaction;

import com.company.office.entity.ActionType;
import com.company.office.entity.GroupType;
import com.company.office.service.ToolsService;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestStepAction;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class RequestStepActionEdit extends OfficeEditor<RequestStepAction> {

    @Inject
    private ToolsService toolsService;

    @Named("fieldGroup.type")
    private LookupField typeField;

    @Named("fieldGroup.message")
    private ResizableTextArea messageField;

    @Named("fieldGroup.description")
    private ResizableTextArea descriptionField;

    @Inject
    private LookupField lookupTemplate;

    @Inject
    private FileUploadField uploadFile;

    @Inject
    private ExportDisplay exportDisplay;

    @Override
    public void init(Map<String, Object> params) {
        addListeners();
        setUserInterface();
        super.additional();
    }

    @Override
    protected void postInit() {
        if (typeField.getValue() == null) {
            typeField.setValue(ActionType.sendFile);
        }
        processActionType(typeField.getValue());
    }

    private void setUserInterface() {
        uploadFile.setClearButtonCaption("");
        uploadFile.setUploadButtonCaption("");

        if (!toolsService.isAdmin()) {
            typeField.setEnabled(false);
            lookupTemplate.setEnabled(false);
        }

        if (toolsService.getActiveGroupType().equals(GroupType.Applicants)) {
            descriptionField.setEnabled(false);
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
        uploadFile.setRequired(actionType.equals("file"));
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
}