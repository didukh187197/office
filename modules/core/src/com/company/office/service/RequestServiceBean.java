package com.company.office.service;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
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

    @Inject
    private Messages messages;

    @Override
    public void nextStep(Request request) {
        Step step = request.getStep().getStep();
        State state;

        if ( (step != null) && (step.equals(officeConfig.getFinalStep())) ) {
            state = State.Closed;
        } else {
            //request.setStep(getNextStep(step));
            state = State.Waiting;
        }

        fixStepChange(request, null, state);
    }

    @Override
    public boolean setWorker(Request request) {
        Request rq = getRequestByID(request.getId());

        //User worker = getFreeUser(rq.getStep());
        User worker = getFreeUser(null);
        if (worker == null) {
            return false;
        }

        fixStepChange(rq, worker, State.Processing);
        return true;
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

    private void commitEntity(Entity entity) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(entity);
        dataManager.commit(commitContext);
    }

    private void fixStepChange(Request request, User worker, State state) {
        /*
        Step step = request.getStep();

        request.setStep(step);
        request.setUser(worker);
        request.setState(state);
        request.setPenalty(null);

        RequestStep requestStep = new RequestStep();
        requestStep.setRequest(request);
        requestStep.setStep(step);
        requestStep.setUser(worker);
        requestStep.setDescription(messages.getMessage(state));

        if (worker != null) {
            List<StepAction> stepActions = getStepByID(step.getId()).getActions();
            for (StepAction sa : stepActions) {
                RequestAction requestAction = new RequestAction();
                requestAction.setRequest(request);
                requestAction.setStep(step);
                requestAction.setUser(worker);
                requestAction.setDescription(sa.getDescription());
                requestAction.setType(sa.getType());
                if (sa.getType().equals(ActionType.sendFile)) {
                    requestAction.setTemplate(sa.getTemplate());
                }
                request.getActions().add(requestAction);
            }
            requestStep.setDescription("Assigned to " + worker.getName() + (stepActions.size() != 0 ? ". Actions added" : "") );
        }
        request.getSteps().add(requestStep);

        commitEntity(request);
        */
    }

    private User getFreeUser(Step step) {
        LoadContext<StepUser> loadContext = LoadContext.create(StepUser.class)
                .setQuery(LoadContext.createQuery("select su from office$StepUser su where su.step.id = :st").setParameter("st", step))
                .setView("stepUser-view");

        double minRate = Double.MAX_VALUE;
        StepUser resStepUser = null;

        List<StepUser> stepsUsers = dataManager.loadList(loadContext);
        for (StepUser su : stepsUsers) {
            double count = su.getRequests() == null ? 0.0 : su.getRequests();
            if (count < su.getThreshold()) {
                double suRate = count / su.getThreshold();
                if (suRate < minRate) {
                    minRate = suRate;
                    resStepUser = su;
                }
            }
        }

        if (resStepUser != null) {
            int count = resStepUser.getRequests() == null ? 0 : resStepUser.getRequests();
            resStepUser.setRequests(count + 1);
            commitEntity(resStepUser);

            return resStepUser.getUser();
        } else {
            return null;
        }
    }

}