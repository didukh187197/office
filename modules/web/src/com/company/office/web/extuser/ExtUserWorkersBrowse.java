package com.company.office.web.extuser;

import com.company.office.OfficeConfig;
import com.company.office.entity.ExtUser;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class ExtUserWorkersBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<ExtUser, UUID> extUsersDs;

    @Inject
    private OfficeConfig officeConfig;

    @Override
    public void init(Map<String, Object> params) {
        extUsersDs.setQuery(officeConfig.getWorkersGroupQuery());
    }
}