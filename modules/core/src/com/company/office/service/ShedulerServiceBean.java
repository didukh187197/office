package com.company.office.service;

import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.entity.RequestStep;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.app.Authenticated;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(ShedulerService.NAME)
public class ShedulerServiceBean implements ShedulerService {

    @Inject
    private OfficeTools officeTools;

    @Inject
    private DataManager dataManager;

    @Authenticated
    public void checkProcessingDelay() {
        long curDateMS = officeTools.addDaysToNow(0).getTime();
        CommitContext commitContext = new CommitContext();

        LoadContext<Request> loadContext = LoadContext.create(Request.class)
                .setQuery(LoadContext.createQuery("select e from office$Request e"))
                .setView("request-view");

        List<Request> requests = dataManager.loadList(loadContext);
        for (Request request: requests) {
            RequestStep step = request.getStep();
            int penalty = officeTools.getCountInt(step.getPenalty());

            switch (step.getState()) {
                case Approving:
                    if (curDateMS > step.getApprovalTerm().getTime()) {
                        step.setPenalty(penalty + 1);
                    }
                    break;
                case Waiting:
                    if (curDateMS > step.getSubmissionTerm().getTime()) {
                        step.setPenalty(penalty - 1);
                    }
                    break;
            }
            commitContext.addInstanceToCommit(step);
        }

        dataManager.commit(commitContext);
    }
}