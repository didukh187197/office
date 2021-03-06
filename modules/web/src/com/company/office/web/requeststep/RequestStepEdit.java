package com.company.office.web.requeststep;

import com.company.office.common.OfficeTools;
import com.company.office.entity.*;
import com.company.office.web.officeeditor.OfficeEditor;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestStepEdit extends OfficeEditor<RequestStep> {

    @Inject
    private OfficeTools officeTools;

    @Named("fieldGroup.penalty")
    private TextField penaltyField;

    @Inject
    private FieldGroup fieldGroupDates;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        super.postInit();

        int penalty = officeTools.getCountInt(getItem().getPenalty());
        if (penalty < 0) {
            penaltyField.setStyleName("applicant-penalty");
        } else
            if (penalty > 0) {
                penaltyField.setStyleName("worker-penalty");
            }

        List<State> workStates = new ArrayList<>();
        workStates.add(State.Waiting);
        workStates.add(State.Approving);

        if ((!officeTools.isAdmin()) && (!workStates.contains(getItem().getState()))) {
            penaltyField.setVisible(false);
            fieldGroupDates.setVisible(false);
            getDialogOptions().setHeight(getDialogOptions().getHeight() - 120);
        }

        if ((readOnly) || (!officeTools.isAdmin())) {
            getComponentNN("okBtn").setVisible(false);
            ((Button) getComponentNN("closeBtn")).setCaption(getMessage("closeBtn"));
            getComponentNN("tabSheet").setEnabled(false);
        }
    }

}