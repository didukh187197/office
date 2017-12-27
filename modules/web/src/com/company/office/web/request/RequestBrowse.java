package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.company.office.entity.Request;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RequestBrowse extends EntityCombinedScreen {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> applicantDs;

    @Inject
    private CollectionDatasource<User, UUID> workerDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (officeConfig.getApplicantsGroup() != null) {
            applicantDs.setQuery(officeConfig.getApplicantsGroupQuery());
        }

        if (officeConfig.getWorkersGroupQuery() != null) {
            workerDs.setQuery(officeConfig.getWorkersGroupQuery());
        }

    }

    public Component snGenerator(Request request) {
        String res = "";
        if (request.getSeries() != null) {
            res += request.getSeries() + "-";
        }

        if (request.getNumber() != null) {
            res += request.getNumber();
        }

        return new Table.PlainTextCell(res);
    }

}