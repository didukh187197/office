package com.company.office.web.applicant;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.GroupBoxLayout;

import javax.inject.Inject;
import java.util.Map;

public class ApplicantBrowse extends AbstractLookup {

    @Inject
    private GroupBoxLayout requestBox;

    @Inject
    private FieldGroup requestFields;

    @Override
    public void init(Map<String, Object> params) {
        requestBox.setWidth(String.valueOf(requestFields.getWidth()));
    }
}