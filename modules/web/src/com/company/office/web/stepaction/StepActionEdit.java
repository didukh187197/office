package com.company.office.web.stepaction;

import com.company.office.entity.ActionType;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.StepAction;
import com.haulmont.cuba.gui.components.LookupField;

import javax.inject.Named;
import java.util.Map;

public class StepActionEdit extends AbstractEditor<StepAction> {
    @Named("fieldGroup.type")
    private LookupField typeField;

    @Override
    protected void postInit() {
        if (typeField.getValue() == null)
            typeField.setValue(ActionType.fromId("file"));
    }
}