package com.company.office.web.screens;

import com.company.office.OfficeConfig;
import com.company.office.common.OfficeTools;
import com.haulmont.cuba.gui.app.security.user.edit.UserEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.UserRole;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtUserEditor extends UserEditor {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private OfficeTools officeTools;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (officeTools.isAdmin())
            return;

        getComponent("rolesPanel").setEnabled(false);
    }

    @Override
    protected void createGroupField() {
        if (officeTools.isAdmin()) {
            super.createGroupField();
            return;
        }

        FieldGroup.FieldConfig groupFc = fieldGroupRight.getFieldNN("group");
        LookupPickerField lookupPickerField = factory.createComponent(LookupPickerField.class);

        lookupPickerField.setDatasource(groupFc.getTargetDatasource(), groupFc.getProperty());
        lookupPickerField.setRequired(true);
        lookupPickerField.setRequiredMessage(getMessage("groupMsg"));

        List<Group> list = new ArrayList<>();
        switch (officeTools.getActiveGroupType()) {
            case Registrators:
                list.add(officeConfig.getApplicantsGroup());
                break;
            case Managers:
                list.add(officeConfig.getRegistratorsGroup());
                list.add(officeConfig.getWorkersGroup());
                list.add(officeConfig.getApplicantsGroup());
                break;
        }
        lookupPickerField.setOptionsList(list);
        groupFc.setComponent(lookupPickerField);
    }

    @Override
    protected boolean preCommit() {
        for (UserRole ur : new ArrayList<>(rolesDs.getItems())) {
            rolesDs.removeItem(ur);
        }

        UserRole userRole = new UserRole();
        userRole.setUser(getItem());
        switch (officeTools.getGroupType(getItem())) {
            case Registrators:
                userRole.setRole(officeConfig.getRegistratorsRole());
                break;
            case Managers:
                userRole.setRole(officeConfig.getManagersRole());
                break;
            case Applicants:
                userRole.setRole(officeConfig.getApplicantsRole());
                break;
            case Workers:
                userRole.setRole(officeConfig.getWorkersRole());
                break;
        }
        rolesDs.addItem(userRole);

        return super.preCommit();
    }
}
