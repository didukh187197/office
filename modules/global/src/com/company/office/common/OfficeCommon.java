package com.company.office.common;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.security.entity.User;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("office_OfficeCommon")
public class OfficeCommon {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private OfficeTools officeTools;

    @Inject
    private DataManager dataManager;

    @Inject
    private Messages messages;

    public void changePosition(Request request) {
        RequestStep newStepByPosition = newStepByPosition(request);
        request.setStep(newStepByPosition);
        request.getSteps().add(newStepByPosition);
        request.getLogs().add(
                newLogItem(request, request.getApplicant(), "The new position set: " + newStepByPosition.getPosition().getDescription(), newStepByPosition)
        );
    }

    public boolean changeWorker(Request request) {
        RequestStep newStepByWorker = newStepByWorker(request);
        if (newStepByWorker != null) {
            User worker = newStepByWorker.getUser();
            request.setStep(newStepByWorker);
            request.getSteps().add(newStepByWorker);
            request.getLogs().add(
                    newLogItem(request, request.getApplicant(), "The new worker set: " + worker.getName(), newStepByWorker)
            );
            request.getLogs().add(
                    newLogItem(request, worker, "The new worker set: " + worker.getName(), newStepByWorker)
            );
            return true;
        }
        return false;
    }

    public RequestLog newLogItem(Request request, User recepient, String info, Entity entity) {
        RequestLog requestLog = new RequestLog();
        requestLog.setRequest(request);
        requestLog.setMoment(officeTools.getMoment());
        requestLog.setSender(officeTools.getActiveUser());
        requestLog.setRecepient(recepient == null ? getLogRecepient(request) : recepient);
        if (entity != null) {
            requestLog.setAttachType(entity.getClass().getName());
            requestLog.setAttachID((UUID) entity.getId());
        }
        requestLog.setInfo(info);
        return requestLog;
    }

    private RequestStep newStepByPosition(Request request) {
        Position position = getNextPosition(request.getStep());
        State state = (position.equals(officeConfig.getFinalPosition())) ? State.Closed : State.Suspended;
        return makeNewStep(request, position, state, null);
    }

    private RequestStep newStepByWorker(Request request) {
        if ((request.getStep() == null) || (request.getStep().getPosition() == null))
            return null;

        User worker = findFreePositionUser(request.getStep().getPosition());
        if (worker == null)
            return null;

        return makeNewStep(request, request.getStep().getPosition(), State.Waiting, worker);
    }

    private User getLogRecepient(Request request) {
        User recepient;
        switch (officeTools.getActiveGroupType()) {
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

    private Position getNextPosition(RequestStep step) {
        if (step == null)
            return officeConfig.getInitPosition();

        if (step.getPosition() == null)
            return officeConfig.getInitPosition();

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

    private RequestStep makeNewStep(Request request, Position position, State state, User worker) {
        RequestStep requestStep = new RequestStep();
        requestStep.setRequest(request);
        requestStep.setMoment(officeTools.getMoment());
        requestStep.setPosition(position);
        requestStep.setState(state);
        requestStep.setUser(worker);
        requestStep.setDescription(position.getDescription() + ", " + messages.getMessage(state));

        if (worker != null) {
            long tm = officeTools.getMoment();

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

            requestStep.setSubmissionTerm( officeTools.addDaysToNow(position.getDaysForSubmission() ) );
            requestStep.setDescription("Assigned to " + worker.getName() + (positionActions.size() != 0 ? ". Actions added" : ""));
        }
        return requestStep;
    }

    public void changePositionUserRequestCount(Position position, User user, int count) {
        if (user == null)
            return;

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
            positionUser.setRequests(officeTools.getCountInt(positionUser.getRequests()) + count);
            commitEntity(positionUser);
        }
    }

    private void commitEntity(Entity entity) {
        CommitContext commitContext = new CommitContext();
        commitContext.addInstanceToCommit(entity);
        dataManager.commit(commitContext);
    }

    private User findFreePositionUser(Position position) {
        LoadContext<PositionUser> loadContext = LoadContext.create(PositionUser.class)
                .setQuery(LoadContext.createQuery("select pu from office$PositionUser pu where pu.position.id = :st")
                        .setParameter("st", position)
                )
                .setView("positionUser-view");

        double minRate = Double.MAX_VALUE;
        PositionUser resPositionUser = null;
        List<PositionUser> positionsUsers = dataManager.loadList(loadContext);

        for (PositionUser pu : positionsUsers) {
            double count = officeTools.getCountDouble(pu.getRequests());
            double threshold = officeTools.getCountDouble(pu.getThreshold());

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
