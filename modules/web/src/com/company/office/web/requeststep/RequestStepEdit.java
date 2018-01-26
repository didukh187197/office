package com.company.office.web.requeststep;

import com.company.office.common.OfficeTools;
import com.company.office.entity.*;
import com.company.office.web.officeeditor.OfficeEditor;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class RequestStepEdit extends OfficeEditor<RequestStep> {

    @Inject
    private OfficeTools officeTools;

    @Named("fieldGroup.penalty")
    private TextField penaltyField;

    @Override
    public void init(Map<String, Object> params) {
        penaltyField.setStyleName("name-field");
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (!officeTools.isAdmin()) {
            getComponentNN("okBtn").setEnabled(false);
            getComponentNN("tabSheet").setEnabled(false);
        }
    }

}