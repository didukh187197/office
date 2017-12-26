package com.company.office.web.requeststep;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.RequestStep;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RequestStepEdit extends AbstractEditor<RequestStep> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(fieldGroup.getWidth()).setHeight("240px");

        if (officeConfig.getWorkersGroupQuery() != null) {
            usersDs.setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

}