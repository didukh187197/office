package com.company.office.web.step;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.Step;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class StepEdit extends AbstractEditor<Step> {

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Inject
    private OfficeConfig officeConfig;

    @Override
    public void init(Map<String, Object> params) {
        usersDs.setQuery(officeConfig.getApplicantsGroupQuery());
    }
}