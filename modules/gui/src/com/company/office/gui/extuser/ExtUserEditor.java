package com.company.office.gui.extuser;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.app.security.user.edit.UserEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.security.entity.Group;

import javax.inject.Inject;

public class ExtUserEditor extends UserEditor {

    @Inject
    private FieldGroup fieldGroupApplicant;

    @Inject
    private FieldGroup fieldGroupWorker;

    @Inject
    private GroupBoxLayout extendedBox;

    @Inject
    private OfficeConfig officeConfig;

    @Override
    protected void postInit() {
        Group group = getItem().getGroup();

        if (group == null)
            return;

        if (group.equals(officeConfig.getApplicantsGroup())) {
            extendedBox.setVisible(true);
            fieldGroupApplicant.setVisible(true);
            fieldGroupWorker.setVisible(false);
        } else
            if (group.equals(officeConfig.getWorkersGroup())) {
                extendedBox.setVisible(true);
                fieldGroupApplicant.setVisible(false);
                fieldGroupWorker.setVisible(true);
            } else {
                extendedBox.setVisible(false);
            }
    }
}
