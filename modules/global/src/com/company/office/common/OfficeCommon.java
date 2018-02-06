package com.company.office.common;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
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
    private Metadata metadata;

    @Inject
    private Messages messages;

    public void changePosition(Request request) {
        Position position = getNextPosition(request.getStep());
        State state = (position.equals(officeConfig.getFinalPosition())) ? State.Closed : State.Suspended;

        RequestStep newStepByPosition = makeNewStep(request, position, state, null);
        request.setStep(newStepByPosition);
        request.getSteps().add(newStepByPosition);
        request.getLogs().add(
                newLogItem(
                        request,
                        request.getApplicant(),
                        "The new position set: " + newStepByPosition.getPosition().getDescription(),
                        newStepByPosition
                )
        );
    }

    public boolean changeWorker(Request request) {
        if (request.getStep().getState().equals(State.Closed))
            return false;

        User worker = findFreePositionUser(request.getStep().getPosition());
        if (worker == null)
            return false;

        RequestStep newStepByWorker = makeNewStep(request, request.getStep().getPosition(), State.Waiting, worker);

        if (newStepByWorker != null) {
            String workerStr = "The new worker set: " + worker.getName();
            request.setStep(newStepByWorker);
            request.getSteps().add(newStepByWorker);
            request.getLogs().add(
                    newLogItem(
                            request,
                            request.getApplicant(),
                            workerStr,
                            newStepByWorker
                    )
            );
            request.getLogs().add(
                    newLogItem(
                            request,
                            worker,
                            workerStr,
                            newStepByWorker
                    )
            );
            return true;
        }
        return false;
    }

    public void changeState(Request request, State newState, String reason) {
        User oldUser = request.getStep().getUser();
        String reasonStr = "The new state set: " + messages.getMessage(newState) + ". " + "Reason: " + reason;

        RequestStep newStepByState = makeNewStep(request, request.getStep().getPosition(), newState, null);
        request.setStep(newStepByState);
        request.getSteps().add(newStepByState);
        request.getLogs().add(
                newLogItem(
                        request,
                        request.getApplicant(),
                        reasonStr,
                        newStepByState
                )
        );
        if (oldUser != null) {
            request.getLogs().add(
                    newLogItem(
                            request,
                            oldUser,
                            reasonStr,
                            newStepByState
                    )
            );
        }
    }

    public RequestLog newLogItem(Request request, User recepient, String info, Entity entity) {
        RequestLog requestLog = metadata.create(RequestLog.class);
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
        if ((step == null) || (step.getPosition() == null))
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
        RequestStep requestStep = metadata.create(RequestStep.class);
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
                RequestStepAction requestStepAction = metadata.create(RequestStepAction.class);
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
        if ((user == null) || (position == null))
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
