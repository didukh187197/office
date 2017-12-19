package com.company.office.web.requeststatus;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.RequestStatus;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class RequestStatusEdit extends AbstractEditor<RequestStatus> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Inject
    private FieldGroup fieldGroupLarge;

    @Named("fieldGroupNarrow.date")
    private DateField dateField;

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(fieldGroupLarge.getWidth()).setHeight("280px");

        if (officeConfig.getWorkersGroupQuery() != null) {
            usersDs.setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            dateField.setValue(new Date());
        }
    }
}