package com.company.office.web.positionaction;

import com.company.office.entity.ActionType;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.PositionAction;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PositionActionEdit extends OfficeEditor<PositionAction> {

    @Named("fieldGroup.type")
    private LookupField typeField;

    @Inject
    private GroupBoxLayout boxFile;

    @Inject
    private LookupField lookupFile;

    @Inject
    private LinkButton btnFile;

    @Inject
    private ExportDisplay exportDisplay;

    @Override
    public void init(Map<String, Object> params) {
        typeField.addValueChangeListener(e -> processActionType((ActionType) e.getValue()));
        lookupFile.addValueChangeListener(e -> setButtonParams());
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (typeField.getValue() == null)
            typeField.setValue(ActionType.sendFile);

        processActionType(typeField.getValue());
    }

    public void onBtnFileClick() {
        if (getItem().getTemplate() == null)
            return;

        exportDisplay.show(getItem().getTemplate(), ExportFormat.OCTET_STREAM);
    }

    private void processActionType(ActionType type) {
        if (type.equals(ActionType.sendFile)) {
            boxFile.setVisible(true);
            lookupFile.setRequired(true);
        } else {
            boxFile.setVisible(false);
            lookupFile.setRequired(false);
            getItem().setTemplate(null);
        }
        setButtonParams();
    }

    private void setButtonParams() {
        btnFile.setEnabled(getItem().getTemplate() != null);
    }
}