package com.company.office.web.request;

import com.company.office.common.RequestProcessing;
import com.company.office.entity.*;
import com.company.office.common.OfficeTools;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.company.office.web.screens.DialogScreen;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.*;

public class RequestBrowse extends AbstractLookup {

    @Inject
    private OfficeTools officeTools;

    @Inject
    private RequestProcessing requestProcessing;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private ToolsService toolsService;

    @Inject
    private GroupDatasource<Request, UUID> requestsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private GroupTable<Request> table;

    @Inject
    private TabSheet tabSheet;

    @Inject
    private LookupField stepLookup;

    @Override
    public void init(Map<String, Object> params) {
        addListeners();
        setUserInterface();
    }

    private void addListeners() {
        CreateAction createAction = (CreateAction) table.getActionNN("create");
        createAction.setAfterCommitHandler(e -> {
            requestsDs.refresh();

            Request request = requestsDs.getItem();
            if (request.getStep() != null) {
                if (request.getStep().getUser() != null) {
                    officeWeb.showWarningMessage(this, getMessage("request.assignedTo") + request.getStep().getUser().getName());
                } else {
                    officeWeb.showWarningMessage(this, getMessage("request.noAvailableWorkers") + request.getStep().getPosition().getDescription());
                }
            }
        });

        EditAction editAction = (EditAction) table.getActionNN("edit");
        editAction.setBeforeActionPerformedHandler(() -> {
            State state = requestsDs.getItem().getStep().getState();
            switch (officeTools.getActiveGroupType()) {
                case Workers:
                    if (!state.equals(State.Waiting) && !state.equals(State.Approving)) {
                        officeWeb.showWarningMessage(this, getMessage("edit.notAllowed"));
                        return false;
                    }
                    break;
            }
            return true;
        });

        editAction.setAfterCommitHandler(e -> requestsDs.refresh());

        PopupButton extraActionsBtn = (PopupButton) getComponentNN("extraActionsBtn");
        Image image = (Image) getComponentNN("image");

        List<State> workStates = new ArrayList<>();
        workStates.add(State.Suspended);
        workStates.add(State.Approving);
        workStates.add(State.Waiting);

        List<State> archivedStates = new ArrayList<>();
        archivedStates.add(State.Closed);
        archivedStates.add(State.Cancelled);

        requestsDs.addItemChangeListener(e -> {
            extraActionsBtn.setEnabled(false);

            if ((e.getItem() == null) || (e.getItem().getStep() == null) || (e.getItem().getStep().getState() == null))
                return;

            if (e.getItem().getImageFile() != null) {
                image.setSource(FileDescriptorResource.class).setFileDescriptor(e.getItem().getImageFile());
                image.setVisible(true);
            } else {
                image.setVisible(false);
            }

            if (!table.getSelected().isEmpty()) {
                extraActionsBtn.setEnabled(true);
                State state = e.getItem().getStep().getState();
                extraActionsBtn.getAction("stop").setVisible(workStates.contains(state));
                extraActionsBtn.getAction("start").setVisible(state.equals(State.Stopped));
                extraActionsBtn.getAction("cancel").setVisible(state.equals(State.Stopped));
                extraActionsBtn.getAction("archive").setVisible(archivedStates.contains(state));
            }
            focusOnStep();
        });

        tabSheet.addSelectedTabChangeListener(e -> {
            if (e.getSelectedTab().getName().equals("stepsTab")) {
                focusOnStep();
            }
        });
    }

    private void focusOnStep() {
        if (requestsDs.getItem() == null)
            return;

        if (requestsDs.getItem().getStep() != null) {
            stepLookup.setValue(requestsDs.getItem().getStep());
        }
    }

    private void setUserInterface() {
        if (officeTools.isAdmin()) {
            table.getActionNN("create").setVisible(true);
            table.getActionNN("remove").setVisible(true);
            getComponentNN("extraActionsBtn").setVisible(true);
            return;
        }

        switch (officeTools.getActiveGroupType()) {
            case Registrators:
                table.getActionNN("create").setVisible(true);
                break;
            case Managers:
                getComponentNN("managerBox").setVisible(true);
                break;
            case Workers:
                requestsDs.setQuery(String.format("select e from office$Request e where e.step.user.id = '%s' order by e.moment", officeTools.getActiveUser().getId()));
                getComponentNN("filter").setVisible(false);
                tabSheet.setSelectedTab("stepsTab");
                break;
            case Applicants:
                requestsDs.setQuery(String.format("select e from office$Request e where e.applicant.id = '%s' order by e.moment", officeTools.getActiveUser().getId()));
                getComponentNN("filter").setVisible(false);
                tabSheet.setSelectedTab("stepsTab");
                break;
        }
    }

    public Component snGenerator(Request request) {
        return new Table.PlainTextCell(request.getSN());
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

    public void onStepBtnClick() {
        if (requestsDs.getItem() == null)
            return;

        officeWeb.showStep(this, stepLookup.getValue());
    }

    // Browse actions methods

    private final String STOP_REQUEST = "stop", START_REQUEST = "start", CANCEL_REQUEST = "cancel", ARCHIVE_REQUEST = "archive";

    private void doBrowseAction(String actionId) {
        Map<String, Object> params = new HashMap<>();
        params.put("actionId", actionId);
        DialogScreen dialogScreen = (DialogScreen) openWindow("dialog-screen", WindowManager.OpenType.DIALOG, params);
        dialogScreen.addCloseWithCommitListener(() -> {
            Request request = requestsDs.getItem();
            String reason = officeTools.left(dialogScreen.getReason(), 180);

            switch (actionId) {
                case STOP_REQUEST:
                    if (request.getStep().getUser() != null) {
                        requestProcessing.changePositionUserRequestCount(request.getStep().getPosition(), request.getStep().getUser(), -1);
                    }
                    requestProcessing.changeState(request, State.Stopped, reason);
                    officeWeb.showWarningMessage(this, getMessage("result.stopped"));
                    break;
                case START_REQUEST:
                    requestProcessing.changeState(request, State.Suspended, reason);
                    if (requestProcessing.changeWorker(request)) {
                        requestProcessing.changePositionUserRequestCount(request.getStep().getPosition(), request.getStep().getUser(), 1);
                    }
                    officeWeb.showWarningMessage(this, getMessage("result.started"));
                    break;
                case CANCEL_REQUEST:
                    requestProcessing.changeState(request, State.Cancelled, reason);
                    officeWeb.showWarningMessage(this, getMessage("result.cancelled"));
                    break;
                case ARCHIVE_REQUEST:
                    requestProcessing.changeState(request, State.Archived, reason);
                    toolsService.blockApplicant(request.getApplicant());
                    officeWeb.showWarningMessage(this, getMessage("result.archived"));
                    break;
                default:
            }
            requestsDs.setItem(request);
            getDsContext().commit();
        });
    }

    public void onStop() {
        doBrowseAction(STOP_REQUEST);
    }

    public void onStart() {
        doBrowseAction(START_REQUEST);
    }

    public void onCancel() {
        doBrowseAction(CANCEL_REQUEST);
    }

    public void onArchive() {
        doBrowseAction(ARCHIVE_REQUEST);
    }

    public void onPosition() {
        Map<String, Object> params = new HashMap<>();
        params.put("selectedStep", requestsDs.getItem().getStep());
        openWindow("positions-screen", WindowManager.OpenType.DIALOG, params);
    }
}