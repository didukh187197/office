package com.company.office.web.stepuser;

import com.company.office.OfficeConfig;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.StepUser;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class StepUserEdit extends OfficeEditor<StepUser> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getWorkersGroupQuery() != null) {
            usersDs.setQuery(officeConfig.getWorkersGroupQuery());
        }

        super.additional();
    }

    @Override
    protected boolean preCommit() {
        if (getItem().getThreshold() <= 0) {
            showNotification(getMessage("warn.wrongThreshold"), NotificationType.ERROR);
            return false;
        }

        return true;
    }
}