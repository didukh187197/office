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
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
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
    private CollectionDatasource<User, UUID> workerDs;

    @Inject
    private CollectionDatasource<RequestAction, UUID> actionsDs;

    @Named("extraActionsBtn.findUser")
    private Action extraActionsBtnFindUser;

    @Named("fieldGroup.applicant")
    private PickerField applicantField;

    @Named("fieldsAction.file")
    private FileUploadField fileField;

    @Named("fieldsAction.message")
    private ResizableTextArea messageField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setUserInterface();
        addDsListeners();
        tuneFields();
    }

    private void tuneFields() {
        applicantField.getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            workerDs.setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    @Inject
    private GroupTable<Request> table;

    private void addDsListeners() {
        requestsDs.addItemChangeListener(e -> {
            //extraActionsBtnFindUser.setEnabled(false);

            if (e.getItem() == null)
                return;

            if (e.getItem().getState() == null)
                return;

            getComponentNN("extraActionsBtn").setEnabled(table.getSelected() != null);
            extraActionsBtnFindUser.setEnabled(e.getItem().getState().equals(State.Waiting));
        });

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

        if (!toolsService.isActiveSuper()) {
            tabSheet.getTab("tabSystem").setVisible(false);
            getComponentNN("extraActionsBtn").setVisible(false);
        }

        if (toolsService.getActiveGroup().equals(officeConfig.getRegistratorsGroup())) {
            tabSheet.getTab("stepsTab").setVisible(false);
            tabSheet.getTab("actionsTab").setVisible(false);
            tabSheet.getTab("communicationsTab").setVisible(false);
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
        if ( (applicantField.getValue() != null) && !( toolsService.isApplicant( applicantField.getValue() ) ) ) {
            showNotification(getMessage("warning.notApplicant"), NotificationType.ERROR);
            return false;
        }
        return true;
    }

    @Inject
    private DataManager dataManager;

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
        showMessageDialog("", msg, MessageType.CONFIRMATION);
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