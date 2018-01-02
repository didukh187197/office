package com.company.office.service;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(RequestService.NAME)
public class RequestServiceBean implements RequestService {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private ToolsService toolsService;

    @Inject
    private DataManager dataManager;

    private Step getNextStep(Step step) {
        if (step == null) {
            return officeConfig.getInitStep();
        }

        if (step.equals(officeConfig.getFinalStep())) {
            return officeConfig.getFinalStep();
        }

        LoadContext<Step> loadContext = LoadContext.create(Step.class)
                .setQuery(LoadContext.createQuery("select s from office$Step s order by s.identifier"))
                .setView("step-view");

        Step resStep = null;
        List<Step> steps = dataManager.loadList(loadContext);
        for (Step st :steps) {
            if (st.equals(step)) {
                resStep = steps.get(steps.indexOf(st) + 1);
                break;
            }
        }

        return resStep;
    }

    private Step getStep(UUID stepId) {
        LoadContext<Step> loadContext = LoadContext.create(Step.class).setId(stepId).setView("step-view");
        return dataManager.load(loadContext);
    }

    @Override
    public void newRequestStep(Request request) {
        Step nextStep = getNextStep(request.getStep());

        if (nextStep.equals(officeConfig.getFinalStep())) {
            request.setState(State.Closed);
        } else {
            RequestStep requestStep = new RequestStep();
            requestStep.setRequest(request);
            requestStep.setStep(nextStep);
            requestStep.setUser(toolsService.getActiveUser());
            requestStep.setDescription(nextStep.getDescription());

            List<StepAction> stepActions = getStep(nextStep.getId()).getActions();
            for (StepAction sa: stepActions) {
                RequestAction requestAction = new RequestAction();
                requestAction.setRequest(request);
                requestAction.setStep(nextStep);
                requestAction.setUser(toolsService.getActiveUser());
                requestAction.setDescription(nextStep.getDescription());
                requestAction.setType(sa.getType());
                if (sa.getType().equals(ActionType.sendFile)) {
                    requestAction.setTemplate(sa.getTemplate());
                }
                request.getActions().add(requestAction);
            }

            request.getSteps().add(requestStep);
            request.setState(State.Processing);
            request.setStep(nextStep);
        }

        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(request);
        dataManager.commit(commitContext);
    }

}