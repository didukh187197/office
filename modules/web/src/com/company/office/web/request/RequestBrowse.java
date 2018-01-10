package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
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

    private void addDsListeners() {
        PopupButton extraActionsBtn = (PopupButton) getComponentNN("extraActionsBtn");

        requestsDs.addItemChangeListener(e -> {
            extraActionsBtn.setEnabled(false);

            if (e.getItem() == null)
                return;

            if (e.getItem().getStep() == null)
                return;

            if (e.getItem().getStep().getState() == null)
                return;

            if (!getTable().getSelected().isEmpty()) {
                extraActionsBtn.setEnabled(true);
                extraActionsBtn.getAction("findUser").setEnabled(e.getItem().getStep().getState().equals(State.Waiting));
            }
        });
    }

    private void tuneFields() {
        ((PickerField) getFieldGroup().getField("applicant").getComponent()).getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            ((CollectionDatasource) getDsContext().getNN("workersDs")).setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void setUserInterface() {
        if (toolsService.isAdmin())
            return;

        TabSheet tabSheet = (TabSheet) getComponentNN("tabSheet");
        tabSheet.getTab("systemTab").setVisible(false);

        Table requestsTable = (Table) getTable();
        Table stepsTable = (Table) getComponentNN("stepsTable");
        Table logsTable = (Table) getComponentNN("logsTable");
        logsTable.getButtonsPanel().setVisible(false);

        switch (toolsService.getActiveGroupType()) {
            case Registrators:
                requestsTable.getActionNN("remove").setVisible(false);
                getComponentNN("extraActionsBtn").setVisible(false);

                tabSheet.getTab("stepsTab").setVisible(false);
                tabSheet.getTab("logsTab").setVisible(false);
            break;

            case Managers:
                requestsTable.getActionNN("create").setVisible(false);
                requestsTable.getActionNN("edit").setVisible(false);
                requestsTable.getActionNN("remove").setVisible(false);
                getComponentNN("extraActionsBtn").setVisible(true);

                stepsTable.getButtonsPanel().setVisible(false);
            break;

            case Workers:
                getComponentNN("buttonsPanel").setVisible(false);
                stepsTable.getButtonsPanel().setVisible(false);

                requestsDs.setQuery(String.format("select e from office$Request e where e.step.user.id = '%s'", toolsService.getActiveUser().getId()));
                getComponentNN("buttonsPanel").setVisible(false);

                tabSheet.setSelectedTab("stepsTab");
            break;

            case Applicants:
                getComponentNN("buttonsPanel").setVisible(false);
                stepsTable.getButtonsPanel().setVisible(false);
            break;
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

            if ( (applicant != null) && !(toolsService.getGroupType(applicant).equals(GroupType.Applicants)) ) {
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
            showMessage("The request is assigned to " + getSelectedRequest().getStep().getUser().getName());
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
                                    requestService.nextPosition(requestsDs.getItem());
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
        showMessage(messages.getMessage(getSelectedRequest().getStep().getState()));
    }

    private void showStep(Position position) {
        if (position == null) {
            showMessage("null");
        } else {
            showMessage(position.getDescription());
        }
    }

    public void onArchive(Component source) {
        openLookup("positions-screen.xml",
                items -> {
                    if (!items.isEmpty()) {
                        for (Object item : items) {
                            showStep(((Position) item));
                        }
                    }
                },
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("selectedStep", getSelectedRequest().getStep())
        );
    }
}