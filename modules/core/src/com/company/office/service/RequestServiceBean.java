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
import java.util.*;

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
        Position position = getNextPosition(request.getStep());
        State state;

        if ( (position != null) && (position.equals(officeConfig.getFinalPosition())) ) {
            state = State.Closed;
        } else {
            state = State.Suspended;
        }

        fixStepChange(request, position, state, null);
    }

    @Override
    public boolean setWorker(Request request) {
        if (request.getStep() == null) {
            return false;
        }

        if (request.getStep().getPosition() == null) {
            return false;
        }

        User newWorker = getNextUser(request.getStep().getPosition());
        if (newWorker == null) {
            return false;
        }
        User oldWorker = request.getStep().getUser();
        Position position = request.getStep().getPosition();

        fixStepChange(request, position, State.Waiting, newWorker);

        if (oldWorker != null) {
            PositionUser oldPU = getPositionUser(position, oldWorker);
            oldPU.setRequests(getCountInt(oldPU.getRequests()) - 1);
            commitEntity(oldPU);
        }

        PositionUser newPU = getPositionUser(position, newWorker);
        newPU.setRequests(getCountInt(newPU.getRequests()) + 1);
        commitEntity(newPU);

        return true;
    }

    @Override
    public void addLogItem(Request request, String info) {
        RequestLog requestLog = new RequestLog();
        requestLog.setRequest(request);
        requestLog.setInfo(info);
        if (request.getLogs() == null) {
            List<RequestLog> logs = new ArrayList<>();
            logs.add(requestLog);
            request.setLogs(logs);
        } else {
            request.getLogs().add(requestLog);
        }
        commitEntity(request);
    }

    private PositionUser getPositionUser(Position position, User user) {
        LoadContext<PositionUser> loadContext = LoadContext.create(PositionUser.class)
                .setQuery(LoadContext.createQuery("select pu from office$PositionUser pu where pu.position.id = :pos and pu.user.id = :usr")
                        .setParameter("pos", position.getId())
                        .setParameter("usr", user.getId())
                )
                .setView("positionUser-view");
        return dataManager.load(loadContext);
    }

    private int getCountInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double getCountDouble(Integer value) {
        return value == null ? 0 : value;
    }

    private Position getNextPosition(RequestStep step) {
        if ((step == null) || (step.getPosition() == null)) {
            return officeConfig.getInitPosition();
        }

        Position position = step.getPosition();
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

    /*
    private Position getPosition(Position position) {
        LoadContext<Position> loadContext = LoadContext.create(Position.class).setId(position.getId()).setView("position-view");
        return dataManager.load(loadContext);
    }
    */

    private void commitEntity(Entity entity) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(entity);
        dataManager.commit(commitContext);
    }

    private void fixStepChange(Request request, Position position, State state, User worker) {
        RequestStep requestStep = new RequestStep();
        requestStep.setRequest(request);
        requestStep.setPosition(position);
        requestStep.setState(state);
        requestStep.setUser(worker);
        requestStep.setDescription(messages.getMessage(state));

        if (worker != null) {
            //List<PositionAction> positionActions = getPosition(position).getActions();
            List<PositionAction> positionActions = position.getActions();
            for (PositionAction pa : positionActions) {
                RequestStepAction requestStepAction = new RequestStepAction();
                requestStepAction.setRequestStep(requestStep);
                requestStepAction.setDescription(pa.getDescription());
                requestStepAction.setType(pa.getType());
                if (pa.getType().equals(ActionType.sendFile)) {
                    requestStepAction.setTemplate(pa.getTemplate());
                }
                if (requestStep.getActions() == null) {
                    List<RequestStepAction> actions = new ArrayList<>();
                    actions.add(requestStepAction);
                    requestStep.setActions(actions);
                } else {
                    requestStep.getActions().add(requestStepAction);
                }
            }

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DAY_OF_YEAR, getCountInt(position.getDaysForSubmission()));
            requestStep.setSubmissionTerm(calendar.getTime());
            requestStep.setDescription("Assigned to " + worker.getName() + (positionActions.size() != 0 ? ". Actions added" : "") );
        }

        if (request.getSteps() == null) {
            List<RequestStep> steps = new ArrayList<>();
            steps.add(requestStep);
            request.setSteps(steps);
        } else {
            request.getSteps().add(requestStep);
        }

        request.setStep(requestStep);
        commitEntity(request);
    }

    private User getNextUser(Position position) {
        LoadContext<PositionUser> loadContext = LoadContext.create(PositionUser.class)
                .setQuery(LoadContext.createQuery("select pu from office$PositionUser pu where pu.position.id = :st").setParameter("st", position))
                .setView("positionUser-view");

        double minRate = Double.MAX_VALUE;
        PositionUser resPositionUser = null;
        List<PositionUser> positionsUsers = dataManager.loadList(loadContext);

        for (PositionUser pu : positionsUsers) {
            double count = getCountDouble(pu.getRequests());
            double threshold = getCountDouble(pu.getThreshold());

            if (count < threshold) {
                double suRate = count / threshold;
                if (suRate < minRate) {
                    minRate = suRate;
                    resPositionUser = pu;
                }
            }
        }

        return resPositionUser != null ? resPositionUser.getUser() : null;
    }

}