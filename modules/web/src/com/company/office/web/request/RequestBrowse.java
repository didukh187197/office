package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
import com.company.office.web.requeststep.RequestStepEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
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
    private GroupDatasource<Request, UUID> requestsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        addListeners();
        tuneComponents();
        setUserInterface();
    }

    private void addListeners() {
        PopupButton extraActionsBtn = (PopupButton) getComponentNN("extraActionsBtn");

        requestsDs.addItemChangeListener(e -> {
            extraActionsBtn.setEnabled(false);

            if ((e.getItem() == null) || (e.getItem().getStep() == null) || (e.getItem().getStep().getState() == null))
                return;

            if (!getTable().getSelected().isEmpty()) {
                extraActionsBtn.setEnabled(true);
                extraActionsBtn.getAction("findUser").setEnabled(e.getItem().getStep().getState().equals(State.Waiting));
            }
        });

        Table actionsTable = (Table) getComponentNN("actionsTable");
        EditAction editStepActionAction = (EditAction) actionsTable.getActionNN("edit");
        Datasource<RequestStepAction> actionsDs = getDsContext().getNN("actionsDs");

        editStepActionAction.setBeforeActionPerformedHandler(() -> {
            if (toolsService.getActiveGroupType().equals(GroupType.Applicants)) {
                if (actionsDs.getItem().getApproved() != null) {
                    showMessage("Action is already approved!");
                    return false;
                }
            }

            Request request = (Request) getFieldGroup().getDatasource().getItem();
            editStepActionAction.setWindowParams(ParamsMap.of("request", request));
            return true;
        });
    }

    private void tuneComponents() {
        ((PickerField) getFieldGroup().getField("applicant").getComponent()).getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            ((CollectionDatasource) getDsContext().getNN("workersDs")).setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void focusOnStep() {
        if (requestsDs.getItem() == null)
            return;

        Table stepsTable = (Table) getComponentNN("stepsTable");
        if ((requestsDs.getItem().getStep() != null) && (!stepsTable.getDatasource().getItems().isEmpty())) {
            stepsTable.setSelected(requestsDs.getItem().getStep());
        }
    }

    private void setStepChildrenButtons(boolean visible) {
        Table actionsTable = (Table) getComponentNN("actionsTable");
        Table communicationsTable = (Table) getComponentNN("communicationsTable");

        actionsTable.getButtonsPanel().setVisible(visible);
        communicationsTable.getButtonsPanel().setVisible(visible);
    }

    private void setUserInterface() {
        TabSheet tabSheet = (TabSheet) getComponentNN("tabSheet");

        tabSheet.addSelectedTabChangeListener(e -> {
            if (e.getSelectedTab().getName().equals("stepsTab")) {
                focusOnStep();
            }
        });

        if (toolsService.isAdmin())
            return;

        tabSheet.getTab("systemTab").setVisible(false);

        Table requestsTable = (Table) getTable();
        requestsTable.getActionNN("step").setVisible(false);
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
                requestsTable.getActionNN("edit").setEnabled(false);
                requestsTable.getActionNN("remove").setVisible(false);
                getComponentNN("extraActionsBtn").setVisible(true);
            break;

            case Workers:
                requestsTable.getActionNN("create").setVisible(false);
                requestsTable.getActionNN("remove").setVisible(false);
                getComponentNN("extraActionsBtn").setVisible(false);
                requestsDs.setQuery(String.format("select e from office$Request e where e.step.user.id = '%s'", toolsService.getActiveUser().getId()));
                getComponentNN("buttonsPanel").setVisible(false);
                focusOnStep();
            break;

            case Applicants:
                requestsTable.getActionNN("create").setVisible(false);
                requestsTable.getActionNN("remove").setVisible(false);
                getComponentNN("extraActionsBtn").setVisible(false);
                requestsDs.setQuery(String.format("select e from office$Request e where e.applicant.id = '%s'", toolsService.getActiveUser().getId()));
                focusOnStep();
            break;
        }
    }

    @Override
    protected void initEditComponents(boolean enabled) {
        super.initEditComponents(enabled);

        TabSheet tabSheet = getTabSheet();
        if (enabled) {
            ComponentsHelper.walkComponents(tabSheet, (component, name) -> {
                if (component instanceof FieldGroup) {
                    component.setEnabled(false);
                }
            });
            getFieldGroup().setEnabled(true);
            getComponentNN("fieldsSystem").setEnabled(true);

            focusOnStep();
        }

        setStepChildrenButtons(enabled);

        switch (toolsService.getActiveGroupType()) {
            case Workers:
            case Applicants:
                tabSheet.getTab("mainTab").setVisible(!enabled);
                tabSheet.getTab("logsTab").setVisible(!enabled);
                getComponentNN("stepsTable").setEnabled(!enabled);
                getComponentNN("stepsTable").setVisible(!enabled);
                getComponentNN("fieldsStep").setVisible(!enabled);
                getComponentNN("fieldsStepDates").setVisible(!enabled);
            break;
            default:
                tabSheet.setSelectedTab("mainTab");
        }
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

    private void tryToAssignUser() {
        if (requestService.setWorker(requestsDs.getItem()) != null) {
            requestsDs.refresh();
            showMessage("The request is assigned to: " + requestsDs.getItem().getStep().getUser().getName());
        } else {
            showMessage("No available workers on the step: " + requestsDs.getItem().getStep().getDescription());
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
                                Request request = (Request) getFieldGroup().getDatasource().getItem();
                                if (creating) {
                                    request = requestService.addLogItem(request, request.getApplicant(), "The new request created");
                                    request = requestService.nextPosition(request);
                                    request = requestService.setWorker(request);
                                } else {
                                    request = requestService.addLogItem(request, request.getApplicant(), "The request edited");
                                }

                                getFieldGroup().getDatasource().setItem(request);
                                super.save();
                                if (creating) {
                                    if (request.getStep() != null) {
                                        if (request.getStep().getUser() != null) {
                                            showMessage("The request is assigned to: " + request.getStep().getUser().getName());
                                        } else {
                                            showMessage("No available workers on the step: " + request.getStep().getDescription());
                                        }
                                    }
                                }
                            }
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void cancelEdit() {
        super.cancel();
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
        showMessage(messages.getMessage(requestsDs.getItem().getStep().getState()));
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
                ParamsMap.of("selectedStep", requestsDs.getItem().getStep().getPosition())
        );
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

    public void onStep(Component source) {
        RequestStepEdit requestStepEdit = (RequestStepEdit) openEditor("office$RequestStep.edit", requestsDs.getItem().getStep(), WindowManager.OpenType.DIALOG);

        /*
        requestStepEdit.addCloseListener((String actionId) -> {
            // do something
        });
        */
    }
}