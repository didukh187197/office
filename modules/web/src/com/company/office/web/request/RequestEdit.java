package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.Request;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class RequestEdit extends AbstractEditor<Request> {

    @Inject
    private OfficeConfig officeConfig;

    @Named("fieldGroup.created")
    private DateField createdField;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    boolean isNew = false;

    @Override
    public void init(Map<String, Object> params) {
        usersDs.setQuery(officeConfig.getWorkersGroupQuery());
    }

    @Override
    protected void initNewItem(Request item) {
        isNew = true;
    }

    @Override
    protected void postInit() {
        if (isNew) {
            createdField.setValue(new Date());
        }
    }
}