package com.company.office.web.positionuser;

import com.company.office.OfficeConfig;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.PositionUser;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class PositionUserEdit extends OfficeEditor<PositionUser> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getWorkersGroup() != null) {
            usersDs.setQuery(
                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getWorkersGroup().getId())
            );
        }
    }

    @Override
    protected void postInit() {
        super.postInit();
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