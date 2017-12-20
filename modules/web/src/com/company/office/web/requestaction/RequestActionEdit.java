package com.company.office.web.requestaction;

import com.company.office.entity.ActionType;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.RequestAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class RequestActionEdit extends AbstractEditor<RequestAction> {

    @Inject
    private FieldGroup fieldGroup;

    @Named("fieldGroup.type")
    private LookupField typeField;

    @Named("fieldGroup.message")
    private ResizableTextArea messageField;

    @Inject
    private VBoxLayout boxFiles;

    @Inject
    private LookupField lookupTemplate;

    @Inject
    private LinkButton btnShowTemplate;

    @Inject
    private FileUploadField uploadFile;

    @Inject
    private ExportDisplay exportDisplay;

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(fieldGroup.getWidth()).setHeight("330px");;
        uploadFile.setClearButtonCaption("");
        uploadFile.setUploadButtonCaption("");

        typeField.addValueChangeListener(e -> processActionType((ActionType) e.getValue()));
        lookupTemplate.addValueChangeListener(e -> setButtonParams());
    }

    @Override
    protected void postInit() {
        if (typeField.getValue() == null) {
            typeField.setValue(ActionType.fromId("file"));
        }
        processActionType(typeField.getValue());
    }

    private void processActionType(ActionType type) {
        String actionType = type.getId();

        boxFiles.setVisible(actionType.equals("file"));
        lookupTemplate.setRequired(actionType.equals("file"));
        messageField.setVisible(actionType.equals("message"));
        messageField.setRequired(actionType.equals("message"));

        setButtonParams();
    }

    private void setButtonParams() {
        btnShowTemplate.setEnabled(getItem().getTemplate() != null);
    }


    public void onBtnShowTemplateClick() {
        if (getItem().getTemplate() == null)
            return;

        exportDisplay.show(getItem().getTemplate(), ExportFormat.OCTET_STREAM);
    }
}