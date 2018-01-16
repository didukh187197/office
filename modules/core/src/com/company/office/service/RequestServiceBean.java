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
    private ToolsService toolsService;

    @Inject
    private DataManager dataManager;

    @Inject
    private Messages messages;

    @Override
    public Request nextPosition(Request request) {
        Position position = getNextPosition(request.getStep());
        State state;

        if (position.equals(officeConfig.getFinalPosition())) {
            state = State.Closed;
        } else {
            state = State.Suspended;
        }

        request = fixStepChange(request, position, state, null);
        request = addLogItemInt(request, request.getApplicant(), "The new position set: " + position.getDescription());

        return request;
    }

    @Override
    public Request setWorker(Request request) {
        if (request.getStep() == null) {
            return request;
        }

        if (request.getStep().getPosition() == null) {
            return request;
        }

        User worker = getNextUser(request.getStep().getPosition());
        if (worker == null) {
            return request;
        }

        Position position = request.getStep().getPosition();
        request = fixStepChange(request, position, State.Waiting, worker);
        request = addLogItemInt(request, request.getApplicant(), "The new worker set: " + worker.getName());
        request = addLogItemInt(request, worker, "The new worker set: " + worker.getName());

        return request;
    }

    @Override
    public RequestLog newLogItem(Request request, User recepient, String info) {
        RequestLog requestLog = new RequestLog();
        requestLog.setRequest(request);
        requestLog.setMoment(toolsService.getMoment());
        requestLog.setSender(toolsService.getActiveUser());
        requestLog.setRecepient(recepient == null ? getRecepient(request) : recepient);
        requestLog.setInfo(info);
        return requestLog;
    }

    private Request addLogItemInt(Request request, User recepient, String info) {
        RequestLog requestLog = newLogItem(request, recepient, info);
        request.getLogs().add(requestLog);
        return request;
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

    private Position getPositionFromDB(Position position) {
        LoadContext<Position> loadContext = LoadContext.create(Position.class).setId(position.getId()).setView("position-view");
        return dataManager.load(loadContext);
    }

    private Request fixStepChange(Request request, Position position, State state, User worker) {
        changePositionUserRequestCount(request, -1);

        RequestStep requestStep = new RequestStep();
        requestStep.setRequest(request);
        requestStep.setMoment(toolsService.getMoment());
        requestStep.setPosition(position);
        requestStep.setState(state);
        requestStep.setUser(worker);
        requestStep.setDescription(position.getDescription() + ", " + messages.getMessage(state));

        if (worker != null) {
            long tm = toolsService.getMoment();

            List<PositionAction> positionActions = getPositionFromDB(position).getActions();
            for (PositionAction pa : positionActions) {
                RequestStepAction requestStepAction = new RequestStepAction();
                requestStepAction.setRequestStep(requestStep);
                requestStepAction.setMoment(tm++);
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
            requestStep.setDescription("Assigned to " + worker.getName() + (positionActions.size() != 0 ? ". Actions added" : ""));
        }

        if (request.getSteps() == null) {
            List<RequestStep> steps = new ArrayList<>();
            steps.add(requestStep);
            request.setSteps(steps);
        } else {
            request.getSteps().add(requestStep);
        }

        request.setStep(requestStep);
        changePositionUserRequestCount(request, 1);

        return request;
    }

    private void changePositionUserRequestCount(Request request, int count) {
        if (request.getStep() == null)
            return;

        User user = request.getStep().getUser();
        if (user == null)
            return;

        Position position = request.getStep().getPosition();
        if (position == null)
            return;

        LoadContext<PositionUser> loadContext = LoadContext.create(PositionUser.class)
                .setQuery(LoadContext.createQuery("select pu from office$PositionUser pu where pu.position.id = :pos and pu.user.id = :usr")
                        .setParameter("pos", position.getId())
                        .setParameter("usr", user.getId())
                )
                .setView("positionUser-view");
        PositionUser positionUser = dataManager.load(loadContext);

        if (positionUser != null) {
            positionUser.setRequests(getCountInt(positionUser.getRequests()) + count);
            commitEntity(positionUser);
        }
    }

    private void commitEntity(Entity entity) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(entity);
        dataManager.commit(commitContext);
    }

    private User getNextUser(Position position) {
        LoadContext<PositionUser> loadContext = LoadContext.create(PositionUser.class)
                .setQuery(LoadContext.createQuery("select pu from office$PositionUser pu where pu.position.id = :st")
                        .setParameter("st", position)
                )
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

    private int getCountInt(Integer value) {
        return value == null ? 0 : value;
    }

    private double getCountDouble(Integer value) {
        return value == null ? 0 : value;
    }

    private User getRecepient(Request request) {
        User recepient;
        switch (toolsService.getActiveGroupType()) {
            case Workers:
                recepient = request.getApplicant();
                break;
            case Applicants:
                recepient = request.getStep().getUser();
                break;
            default:
                recepient = request.getApplicant();
        }
        return recepient;
    }

}