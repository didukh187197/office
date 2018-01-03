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
    private DataManager dataManager;

    @Override
    public void nextStep(Request request) {
        Step step = request.getStep();

        if (step != null) {
            if (step.equals(officeConfig.getFinalStep())) {
                request.setState(State.Closed);
                saveRequest(request);
                return;
            }
        }

        request.setState(State.Waiting);
        request.setStep(getNextStep(step));
        saveRequest(request);
    }

    @Override
    public boolean setWorker(Request request) {
        Request rq = getRequestByID(request.getId());

        User worker = getFreeUser(rq.getStep());
        if (worker == null) {
            return false;
        }

        setStepAndUser(rq, worker);
        return true;
    }

    private void saveRequest(Request request) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(request);
        dataManager.commit(commitContext);
    }

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

    private Request getRequestByID(UUID requestId) {
        LoadContext<Request> loadContext = LoadContext.create(Request.class).setId(requestId).setView("request-view");
        return dataManager.load(loadContext);
    }

    private Step getStepByID(UUID stepId) {
        LoadContext<Step> loadContext = LoadContext.create(Step.class).setId(stepId).setView("step-view");
        return dataManager.load(loadContext);
    }

    private void setStepAndUser(Request request, User user) {
        Step step = request.getStep();

        RequestStep requestStep = new RequestStep();
        requestStep.setRequest(request);
        requestStep.setStep(step);
        requestStep.setUser(user);
        requestStep.setDescription(step.getDescription());

        List<StepAction> stepActions = getStepByID(step.getId()).getActions();
        for (StepAction sa: stepActions) {
            RequestAction requestAction = new RequestAction();
            requestAction.setRequest(request);
            requestAction.setStep(step);
            requestAction.setUser(user);
            requestAction.setDescription(step.getDescription());
            requestAction.setType(sa.getType());
            if (sa.getType().equals(ActionType.sendFile)) {
                requestAction.setTemplate(sa.getTemplate());
            }
            request.getActions().add(requestAction);
        }

        request.getSteps().add(requestStep);
        request.setState(State.Processing);
        request.setStep(step);

        saveRequest(request);
    }

    private User getFreeUser(Step step) {
        LoadContext<StepUser> loadContext = LoadContext.create(StepUser.class)
                .setQuery(LoadContext.createQuery("select su from office$StepUser su where su.step.id = :st").setParameter("st", step))
                .setView("stepUser-view");

        double minRate = Double.MAX_VALUE;
        User resUser = null;

        List<StepUser> stepsUsers = dataManager.loadList(loadContext);
        for (StepUser su : stepsUsers) {
            double count = su.getRequests() == null ? 0.0 : su.getRequests();
            if (count < su.getThreshold()) {
                double suRate = count / su.getThreshold();
                if (suRate < minRate) {
                    minRate = suRate;
                    resUser = su.getUser();
                }
            }
        }

        return resUser;
    }

}