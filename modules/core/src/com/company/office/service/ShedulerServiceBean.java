package com.company.office.service;

import com.company.office.OfficeConfig;
import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.entity.RequestStep;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.app.Authenticated;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(ShedulerService.NAME)
public class ShedulerServiceBean implements ShedulerService {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private OfficeTools officeTools;

    @Inject
    private ToolsService toolsService;

    @Inject
    private DataManager dataManager;

    private Map<User, Integer> userPenalties = new HashMap<>();

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
                        penalty++;
                        step.setPenalty(penalty);
                        storeUserPenaltyToMap(step.getUser(), penalty);
                    }
                    break;
                case Waiting:
                    if (curDateMS > step.getSubmissionTerm().getTime()) {
                        penalty--;
                        step.setPenalty(penalty);
                        storeUserPenaltyToMap(request.getApplicant(), Math.abs(penalty));
                    }
                    break;
            }
            commitContext.addInstanceToCommit(step);
        }
        dataManager.commit(commitContext);
        tryToLockUsers();
    }

    private void storeUserPenaltyToMap(User user, int penalty) {
        if (userPenalties.containsKey(user)) {
            penalty += officeTools.getCountInt(userPenalties.get(user));
        }
        userPenalties.put(user, penalty);
    }

    private void tryToLockUsers() {
        for (Map.Entry entry : userPenalties.entrySet()) {
            User user = (User) entry.getKey();
            int penalty = officeTools.getCountInt((Integer) entry.getValue());
            int maxPenalty = 100;

            switch (officeTools.getGroupType(user)) {
                case Applicants:
                    maxPenalty = officeConfig.getApplicantPenalty();
                    break;
                case Workers:
                    maxPenalty = officeConfig.getWorkerPenalty();
                    break;
            }
            if (penalty > maxPenalty) {
                toolsService.blockUser(user);
            }
        }
    }

}