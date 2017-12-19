package com.company.office.web.requestaction;

import com.company.office.entity.ActionType;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.RequestAction;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;

public class RequestActionEdit extends AbstractEditor<RequestAction> {

    @Inject
    private FieldGroup fieldGroup;

    @Named("fieldGroup.type")
    private LookupField typeField;

    @Named("fieldGroup.created")
    private DateField createdField;

    @Inject
    private GroupBoxLayout boxFiles;

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
        getDialogOptions().setWidth(fieldGroup.getWidth()).setHeight("380px");
        uploadFile.setClearButtonCaption("");
        uploadFile.setUploadButtonCaption("");

        typeField.addValueChangeListener(e -> processActionType((ActionType) e.getValue()));
        lookupTemplate.addValueChangeListener(e -> setButtonParams());
    }

    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            createdField.setValue(new Date());
        }

        if (typeField.getValue() == null) {
            typeField.setValue(ActionType.fromId("file"));
        }
        processActionType(typeField.getValue());
    }

    private void processActionType(ActionType type) {
        if (type == ActionType.fromId("file")) {
            boxFiles.setVisible(true);
            lookupTemplate.setRequired(true);
        } else {
            boxFiles.setVisible(false);
            lookupTemplate.setRequired(false);
            getItem().setTemplate(null);
        }

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