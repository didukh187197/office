package com.company.office.service;

import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.entity.RequestLog;
import com.company.office.entity.State;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

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
    public void blockUser(User applicant) {
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

    @Override
    public int getApplicantPenalty(User applicant) {
        LoadContext<Request> loadContext = LoadContext.create(Request.class)
                .setQuery(LoadContext.createQuery("select e from office$Request e where e.applicant.id = :userId")
                        .setParameter("userId", applicant.getId())
                )
                .setView("request-view");
        Request request = dataManager.load(loadContext);
        if (request == null)
            return 0;

        if (!request.getStep().getState().equals(State.Waiting))
            return 0;

        return Math.abs(officeTools.getCountInt(request.getStep().getPenalty()));
    }

    @Override
    public int getWorkerPenalty(User worker) {
        LoadContext<Request> loadContext = LoadContext.create(Request.class)
                .setQuery(LoadContext.createQuery("select e from office$Request e where e.step.user.id = :userId")
                        .setParameter("userId", worker.getId())
                )
                .setView("request-view");
        List<Request> requests = dataManager.loadList(loadContext);
        if (requests.isEmpty())
            return 0;

        int res = 0;
        for(Request request : requests) {
            if (request.getStep().getState().equals(State.Approving)) {
                res += officeTools.getCountInt(request.getStep().getPenalty());
            }
        }
        return res;
    }
}