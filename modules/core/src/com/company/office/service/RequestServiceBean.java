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
    public void nextPosition(Request request) {
        Position position = request.getStep().getPosition();
        State state;

        if ( (position != null) && (position.equals(officeConfig.getFinalPosition())) ) {
            state = State.Closed;
        } else {
            //request.setStep(getNextStep(position));
            state = State.Waiting;
        }

        fixPositionChange(request, null, state);
    }

    @Override
    public boolean setWorker(Request request) {
        Request rq = getRequestByID(request.getId());

        //User worker = getFreeUser(rq.getPosition());
        User worker = getFreeUser(null);
        if (worker == null) {
            return false;
        }

        fixPositionChange(rq, worker, State.Processing);
        return true;
    }

    private Position getNextPosition(Position position) {
        if (position == null) {
            return officeConfig.getInitPosition();
        }

        if (position.equals(officeConfig.getFinalPosition())) {
            return officeConfig.getFinalPosition();
        }

        LoadContext<Position> loadContext = LoadContext.create(Position.class)
                .setQuery(LoadContext.createQuery("select s from office$Position s order by s.identifier"))
                .setView("position-view");

        Position resPosition = null;
        List<Position> positions = dataManager.loadList(loadContext);
        for (Position st :positions) {
            if (st.equals(position)) {
                resPosition = positions.get(positions.indexOf(st) + 1);
                break;
            }
        }

        return resPosition;
    }

    private Request getRequestByID(UUID requestId) {
        LoadContext<Request> loadContext = LoadContext.create(Request.class).setId(requestId).setView("request-view");
        return dataManager.load(loadContext);
    }

    private Position getPositionByID(UUID positionId) {
        LoadContext<Position> loadContext = LoadContext.create(Position.class).setId(positionId).setView("position-view");
        return dataManager.load(loadContext);
    }

    private void commitEntity(Entity entity) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(entity);
        dataManager.commit(commitContext);
    }

    private void fixPositionChange(Request request, User worker, State state) {
        /*
        Position step = request.getPosition();

        request.setPosition(step);
        request.setUser(worker);
        request.setState(state);
        request.setPenalty(null);

        RequestStep requestStep = new RequestStep();
        requestStep.setRequest(request);
        requestStep.setStep(step);
        requestStep.setUser(worker);
        requestStep.setDescription(messages.getMessage(state));

        if (worker != null) {
            List<PositionAction> stepActions = getPositionByID(step.getId()).getActions();
            for (PositionAction sa : stepActions) {
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

    private User getFreeUser(Position position) {
        LoadContext<PositionUser> loadContext = LoadContext.create(PositionUser.class)
                .setQuery(LoadContext.createQuery("select su from office$PositionUser su where su.position.id = :st").setParameter("st", position))
                .setView("positionUser-view");

        double minRate = Double.MAX_VALUE;
        PositionUser resPositionUser = null;

        List<PositionUser> positionsUsers = dataManager.loadList(loadContext);
        for (PositionUser pu : positionsUsers) {
            double count = pu.getRequests() == null ? 0.0 : pu.getRequests();
            if (count < pu.getThreshold()) {
                double suRate = count / pu.getThreshold();
                if (suRate < minRate) {
                    minRate = suRate;
                    resPositionUser = pu;
                }
            }
        }

        if (resPositionUser != null) {
            int count = resPositionUser.getRequests() == null ? 0 : resPositionUser.getRequests();
            resPositionUser.setRequests(count + 1);
            commitEntity(resPositionUser);

            return resPositionUser.getUser();
        } else {
            return null;
        }
    }

}