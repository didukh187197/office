package com.company.office.service;

import com.company.office.common.OfficeTools;
import com.company.office.entity.RequestLog;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ToolsService.NAME)
public class ToolsServiceBean implements ToolsService {

    @Inject
    private OfficeTools officeTools;

    @Inject
    private DataManager dataManager;

    @Override
    public long unreadLogsCount() {
        LoadContext<RequestLog> loadContext = LoadContext.create(RequestLog.class)
                .setQuery(LoadContext.createQuery("select e from office$RequestLog e where e.recepient.id = :userId and e.read is null")
                        .setParameter("userId", officeTools.getActiveUser().getId())
                )
                .setView("_local");
        return dataManager.getCount(loadContext);
    }

    @Override
    public void blockApplicant(User applicant) {
        User blockedUser =  dataManager.load(LoadContext.create(User.class).setId(applicant.getId()).setView("_local"));
        blockedUser.setActive(false);
        commitEntity(blockedUser);
    }

    @Override
    public void commitEntity(Entity entity) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(entity);
        dataManager.commit(commitContext);
    }
}