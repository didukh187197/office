package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.*;

public class RequestEdit extends AbstractEditor<Request> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private ToolsService toolsService;

    @Inject
    private RequestService requestService;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private TabSheet tabSheet;

    @Override
    public void init(Map<String, Object> params) {
    }

    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            getItem().setMoment(toolsService.getMoment());
        }

        addListeners();
        tuneComponents();
        setUserInterface();

        showSubmitButton();
        showApproveBtn();
    }

    private void addListeners() {
        Table actionsTable = (Table) getComponentNN("actionsTable");
        EditAction editStepActionAction = (EditAction) actionsTable.getActionNN("edit");
        Datasource<RequestStepAction> actionsDs = getDsContext().getNN("actionsDs");

        editStepActionAction.setBeforeActionPerformedHandler(() -> {
            if (toolsService.getActiveGroupType().equals(GroupType.Applicants)) {
                if (actionsDs.getItem().getApproved() != null) {
                    officeWeb.showWarningMessage(this, getMessage("edit.action.alreadyApproved"));
                    return false;
                }
            }

            editStepActionAction.setWindowParams(ParamsMap.of("request", getItem()));
            return true;
        });

        actionsDs.addItemPropertyChangeListener(e -> {
            showSubmitButton();
            showApproveBtn();
        });
    }

    private void tuneComponents() {
        ((PickerField) fieldGroup.getField("applicant").getComponent()).getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            ((CollectionDatasource) getDsContext().getNN("workersDs")).setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void setUserInterface() {
        if (toolsService.isAdmin()) {
            ((FieldGroup) getComponentNN("stepParamsFields")).setEditable(true);
            ((FieldGroup) getComponentNN("stepDatesFields")).setEditable(true);
            ((FieldGroup) getComponentNN("stepOtherFields")).setEditable(true);
            return;
        }

        tabSheet.getTab("systemTab").setVisible(false);

        switch (toolsService.getActiveGroupType()) {
            case Registrators:
                tabSheet.setEnabled(false);
                break;
            case Workers:
                getComponentNN("infoBox").setEnabled(false);
                break;
            case Applicants:
                getComponentNN("infoBox").setEnabled(false);
                break;
            default:
        }
    }

    public Component performedGenerator(RequestStepAction requestStepAction) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);

        checkBox.setValue(false);

        if (requestStepAction.getType() == ActionType.sendFile) {
            if (requestStepAction.getFile() != null) {
                checkBox.setValue(true);
            }
        } else
        if (requestStepAction.getType() == ActionType.sendMessage) {
            if (requestStepAction.getMessage() != null) {
                checkBox.setValue(true);
            }
        }
        return checkBox;
    }

    @Override
    protected boolean preCommit() {
        User applicant = ((PickerField) fieldGroup.getFieldNN("applicant").getComponent()).getValue();

        if ( (applicant != null) && !(toolsService.getGroupType(applicant).equals(GroupType.Applicants)) ) {
            officeWeb.showErrorMessage(this, getMessage("warning.notApplicant"));
            return false;
        }

        Request request = getItem();
        if (PersistenceHelper.isNew(request)) {
            List<RequestLog> logs = new ArrayList<>();
            logs.add(
                    requestService.newLogItem(request, request.getApplicant(), "The new request created", null)
            );
            request.setLogs(logs);

            RequestStep newStepByPosition = requestService.newStepByPosition(request);
            request.setStep(newStepByPosition);

            List<RequestStep> steps = new ArrayList<>();
            steps.add(newStepByPosition);
            request.setSteps(steps);

            request.getLogs().add(
                    requestService.newLogItem(request, request.getApplicant(), "The new position set: " + newStepByPosition.getPosition().getDescription(), newStepByPosition)
            );

            RequestStep newStepByWorker = requestService.newStepByWorker(request);
            if (newStepByWorker != null) {
                User worker = newStepByWorker.getUser();
                if (worker != null) {
                    request.setStep(newStepByWorker);
                    request.getSteps().add(newStepByWorker);

                    request.getLogs().add(
                            requestService.newLogItem(request, request.getApplicant(), "The new worker set: " + worker.getName(), newStepByWorker)
                    );
                    request.getLogs().add(
                            requestService.newLogItem(request, worker, "The new worker set: " + worker.getName(), newStepByWorker)
                    );
                }
                requestService.changePositionUserRequestCount(request, 1);
            }
            setItem(request);

        } else {
            switch (toolsService.getActiveGroupType()) {
                case Workers:
                case Applicants:
                    checkSubmitApprove();
                    break;
                default:
                    request.getLogs().add(
                            requestService.newLogItem(request, request.getApplicant(), "The request edited", null)
                    );
                    if (request.getStep().getUser() != null) {
                        request.getLogs().add(
                                requestService.newLogItem(request, request.getStep().getUser(), "The request edited", null)
                        );
                    }
                    break;
            }
        }


        return true;
    }

    private void showSubmitButton() {
        getComponentNN("submitBtn").setVisible(false);

        if (!toolsService.getActiveGroupType().equals(GroupType.Applicants))
            return;

        Request request = getItem();
        if ((request.getStep()) == null)
            return;

        List<RequestStepAction> requestStepActions = request.getStep().getActions();
        if ((requestStepActions == null) || (requestStepActions.size() == 0))
            return;

        int submitted = 0;
        for (RequestStepAction requestStepAction: requestStepActions) {
            if (requestStepAction.getSubmitted() != null)
                submitted++;
        }

        getComponentNN("submitBtn").setVisible(submitted == requestStepActions.size());
    }

    private void showApproveBtn() {
        getComponentNN("approveBtn").setVisible(false);

        if (!toolsService.getActiveGroupType().equals(GroupType.Workers))
            return;

        Request request = getItem();
        if ((request.getStep()) == null)
            return;

        List<RequestStepAction> requestStepActions = request.getStep().getActions();
        if ((requestStepActions == null) || (requestStepActions.size() == 0))
            return;

        int approved = 0;
        for (RequestStepAction requestStepAction: requestStepActions) {
            if (requestStepAction.getApproved() != null)
                approved++;
        }

        getComponentNN("approveBtn").setVisible(approved == requestStepActions.size());
    }

    private void checkSubmitApprove() {
        Request request = getItem();

        if ((request.getStep()) == null)
            return;

        List<RequestStepAction> requestStepActions = request.getStep().getActions();
        if ((requestStepActions == null) || (requestStepActions.size() == 0))
            return;

        int submitted = 0;
        int approved = 0;

        for (RequestStepAction requestStepAction: requestStepActions) {
            if (requestStepAction.getSubmitted() != null)
                submitted++;

            if (requestStepAction.getApproved() != null)
                approved++;
        }

        switch (toolsService.getActiveGroupType()) {
            case Workers:
                if (approved == requestStepActions.size()) {
                    request.getStep().setApproved(new Date());
                }
                break;
            case Applicants:
                if (submitted == requestStepActions.size()) {
                    RequestStep requestStep = request.getStep();
                    requestStep.setSubmitted(new Date());
                    requestStep.setApprovalTerm(toolsService.addDaysToNow(request.getStep().getPosition().getDaysForSubmission()));
                }
                break;
            default:
                break;
        }
    }

    public void onSubmitBtnClick() {
        showOptionDialog(
                "",
                getMessage("edit.submitAllActions"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            Request request = getItem();
                            RequestStep requestStep = request.getStep();
                            requestStep.setSubmitted(new Date());
                            requestStep.setApprovalTerm(toolsService.addDaysToNow(requestStep.getPosition().getDaysForSubmission()));
                            requestStep.setState(State.Approving);
                            request.getLogs().add(
                                    requestService.newLogItem(request, requestStep.getUser(), "The request submitted", null)
                            );
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onApproveBtnClick() {
    }

}