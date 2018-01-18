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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    }

    private void tuneComponents() {
        ((PickerField) fieldGroup.getField("applicant").getComponent()).getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            ((CollectionDatasource) getDsContext().getNN("workersDs")).setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void setUserInterface() {
        if (toolsService.isAdmin())
            return;

        tabSheet.getTab("systemTab").setVisible(false);

        switch (toolsService.getActiveGroupType()) {
            case Registrators:
                tabSheet.setEnabled(false);
                break;

            case Workers:
                break;

            case Applicants:
                break;
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
            logs.add(requestService.newLogItem(request, request.getApplicant(), "The new request created", null));
            request.setLogs(logs);
            request = requestService.nextPosition(request);
            request = requestService.setWorker(request);
        } else {
            switch (toolsService.getActiveGroupType()) {
                case Workers:
                case Applicants:
                    break;
                default:
                    request.getLogs().add(requestService.newLogItem(request, request.getApplicant(), "The request edited", null));
                    if (request.getStep().getUser() != null) {
                        request.getLogs().add(requestService.newLogItem(request, request.getStep().getUser(), "The request edited", null));
                    }
                    break;
            }

            if (checkSubmitApprove()) {
                officeWeb.showWarningMessage(this, "Bu");
                request.getStep().setSubmitted(new Date());
                request.getStep().setApprovalTerm(toolsService.addDaysToNow(request.getStep().getPosition().getDaysForSubmission()));
            }
        }

        if ( toolsService.isAdmin() ) {
            setItem(request);
        }
        return true;
    }

    private boolean checkSubmitApprove() {
        Request request = getItem();

        if ((request.getStep()) == null)
            return false;

        List<RequestStepAction> requestStepActions = request.getStep().getActions();
        if ((requestStepActions == null) || (requestStepActions.size() == 0))
            return false;

        int submitted = 0;
        int approved = 0;

        for (RequestStepAction requestStepAction: requestStepActions) {
            if (requestStepAction.getSubmitted() != null)
                submitted++;

            if (requestStepAction.getApproved() != null)
                approved++;
        }

        final boolean[] res = {false};
        switch (toolsService.getActiveGroupType()) {
            case Workers:
                if (approved == requestStepActions.size()) {
                    showOptionDialog(
                            "",
                            getMessage("edit.approveAllActions"),
                            MessageType.CONFIRMATION,
                            new Action[] {
                                    new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                                        getItem().getStep().setApproved(new Date());
                                    }),
                                    new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                            }
                    );
                }
                break;
            case Applicants:
                if (submitted == requestStepActions.size()) {
                    showOptionDialog(
                            "",
                            getMessage("edit.submitAllActions"),
                            MessageType.CONFIRMATION,
                            new Action[] {
                                    new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                                        res[0] = true;
                                    }),
                                    new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                            }
                    );
                }
                break;
            default:
                break;
        }

        return res[0];
    }

}