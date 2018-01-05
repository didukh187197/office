package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RequestBrowse extends EntityCombinedScreen {

    @Inject
    private ToolsService toolsService;

    @Inject
    private RequestService requestService;

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private DataManager dataManager;

    @Inject
    private GroupDatasource<Request, UUID> requestsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addDsListeners();
        tuneFields();
        setUserInterface();

        /*
        getDsContext().addBeforeCommitListener(context -> {
            if (customer != null)
                context.getCommitInstances().add(customer);
        }
        */
    }

    private void tuneFields() {
        ((PickerField) getFieldGroup().getField("applicant").getComponent()).getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            ((CollectionDatasource) getDsContext().getNN("workerDs")).setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void addDsListeners() {
        PopupButton extraActionsBtn = (PopupButton) getComponentNN("extraActionsBtn");
        CollectionDatasource<RequestAction, UUID> actionsDs = ((Table) getComponentNN("actionsTable")).getDatasource();

        requestsDs.addItemChangeListener(e -> {
            extraActionsBtn.setEnabled(false);

            if (e.getItem() == null)
                return;

            if (e.getItem().getState() == null)
                return;

            if (!getTable().getSelected().isEmpty()) {
                extraActionsBtn.setEnabled(true);
                extraActionsBtn.getAction("findUser").setEnabled(e.getItem().getState().equals(State.Waiting));
            }
        });

        FieldGroup fieldsAction = (FieldGroup) getComponentNN("fieldsAction");
        Component fileField = fieldsAction.getFieldNN("file").getComponent();
        Component messageField = fieldsAction.getFieldNN("message").getComponent();

        actionsDs.addItemChangeListener(e -> {
            if (actionsDs.getItem() != null) {
                ActionType actionType = actionsDs.getItem().getType();

                if (actionType != null) {
                    fileField.setVisible(actionType.equals(ActionType.sendFile));
                    messageField.setVisible(actionType.equals(ActionType.sendMessage));
                }
            }
        });
    }

    private void setUserInterface() {
        TabSheet tabSheet = (TabSheet) getComponentNN("tabSheet");

        Table actionsTable = (Table) getComponentNN("actionsTable");
        CollectionDatasource<RequestAction, UUID> actionsDs = actionsTable.getDatasource();
        EditAction actionsTableEdit = (EditAction) actionsTable.getAction("edit");
        RemoveAction actionsTableRemove = (RemoveAction) actionsTable.getAction("remove");

        Table communicationsTable = (Table) getComponentNN("communicationsTable");
        CollectionDatasource<RequestCommunication, UUID> communicationsDs = communicationsTable.getDatasource();
        EditAction communicationsTableEdit = (EditAction) communicationsTable.getAction("edit");
        RemoveAction communicationsTableRemove = (RemoveAction) communicationsTable.getAction("remove");

        if (!toolsService.isActiveSuper()) {
            tabSheet.getTab("tabSystem").setVisible(false);
        }

        Group userGroup = toolsService.getActiveGroup();

        if (userGroup.equals(officeConfig.getRegistratorsGroup())) {
            tabSheet.getTab("stepsTab").setVisible(false);
            tabSheet.getTab("actionsTab").setVisible(false);
            tabSheet.getTab("communicationsTab").setVisible(false);

            getComponentNN("extraActionsBtn").setVisible(false);
        }
        else if (userGroup.equals(officeConfig.getManagersGroup())) {
            getComponentNN("extraActionsBtn").setVisible(true);
        }
        else if (userGroup.equals(officeConfig.getWorkersGroup())) {
            requestsDs.setQuery(String.format("select e from office$Request e where e.user.id = '%s'", toolsService.getActiveUser().getId()));
            getComponentNN("buttonsPanel").setVisible(false);

            actionsTableRemove.setBeforeActionPerformedHandler(() -> {
                showMessage("" + actionsDs.getItem().getCreatedBy());
                return false;
            });

            communicationsTableRemove.setBeforeActionPerformedHandler(() -> {
                showMessage("" + communicationsDs.getItem().getCreatedBy());
                return false;
            });

            tabSheet.setSelectedTab("actionsTab");
        }
        else if (userGroup.equals(officeConfig.getWorkersGroup())) {

        }
    }

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);

        TabSheet tabSheet = (TabSheet) getComponentNN("tabSheet");
        Group userGroup = toolsService.getActiveGroup();

        if (userGroup.equals(officeConfig.getWorkersGroup())) {
            tabSheet.getTab("mainTab").setVisible(!enabled);
            tabSheet.getTab("stepsTab").setVisible(!enabled);
        }
    }

    public Component snGenerator(Request request) {
        String res = "";
        if (request.getSeries() != null) {
            res += request.getSeries() + "-";
        }

        if (request.getNumber() != null) {
            res += request.getNumber();
        }

        return new Table.PlainTextCell(res);
    }

    private boolean preSave() {
        if (getComponentNN("mainTab").isVisible()) {
            User applicant = ((PickerField) getFieldGroup().getField("applicant").getComponent()).getValue();

            if ( (applicant != null) && !(toolsService.isApplicant(applicant)) ) {
                showNotification(getMessage("warning.notApplicant"), NotificationType.ERROR);
                return false;
            }
        }
        return true;
    }

    private Request getSelectedRequest() {
        LoadContext<Request> loadContext = LoadContext.create(Request.class).setId(requestsDs.getItem().getId()).setView("request-view");
        return dataManager.load(loadContext);
    }

    private void tryToAssignUser() {
        if (requestService.setWorker(requestsDs.getItem())) {
            showMessage("The request is assigned to " + getSelectedRequest().getUser().getName());
        } else {
            showMessage("No available workers on step: " + getSelectedRequest().getStep().getDescription());
        }
    }

    public void saveWithPrompt() {
        showOptionDialog(
                "",
                messages.getMainMessage("dialog.saveAndClose.title"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            if (preSave()) {
                                boolean isNew = PersistenceHelper.isNew(getFieldGroup().getDatasource().getItem());

                                super.save();

                                if (isNew) {
                                    requestService.nextStep(requestsDs.getItem());
                                    tryToAssignUser();
                                }
                                requestsDs.refresh();
                            }
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    private void showMessage(String msg) {
        //showMessageDialog("", msg, MessageType.CONFIRMATION);
        showNotification(msg, NotificationType.WARNING);
    }

    public void onFindUser(Component source) {
        tryToAssignUser();
        requestsDs.refresh();
    }

    public void onChangeStep(Component source) {
    }

    public void onSuspend(Component source) {
    }

    public void onResume(Component source) {
    }

    public void onCancel(Component source) {
        showMessage(messages.getMessage(getSelectedRequest().getState()));
    }

    private void showStep(Step step) {
        if (step == null) {
            showMessage("null");
        } else {
            showMessage(step.getDescription());
        }
    }

    public void onArchive(Component source) {
        openLookup("step-screen",
                items -> {
                    if (!items.isEmpty()) {
                        for (Object item : items) {
                            showStep(((Step) item));
                        }
                    }
                },
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("selectedStep", getSelectedRequest().getStep())
        );
    }
}