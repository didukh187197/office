package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.company.office.service.RequestService;
import com.company.office.service.ToolsService;
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

    /*
    @Inject
    private CollectionDatasource<User, UUID> applicantDs;
    */

    @Inject
    private GroupDatasource<Request, UUID> requestsDs;

    @Inject
    private CollectionDatasource<User, UUID> workerDs;

    @Inject
    private CollectionDatasource<RequestAction, UUID> actionsDs;

    @Named("table.setStep")
    private Action tableSetStep;

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
        /*
        if (officeConfig.getApplicantsGroup() != null) {
            applicantDs.setQuery(officeConfig.getApplicantsGroupQuery());
        }
        */
        applicantField.getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            workerDs.setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void addDsListeners() {
        requestsDs.addItemChangeListener(e -> {
            tableSetStep.setEnabled(false);

            if (e.getItem() == null)
                return;

            if (e.getItem().getState() == null)
                return;

            tableSetStep.setEnabled(e.getItem().getState().equals(State.Waiting));
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
        if (!toolsService.isActiveSuper()) {
            ((TabSheet) getComponentNN("tabSheet")).getTab("tabSystem").setVisible(false);
        }

        if (toolsService.getActiveGroup().equals(officeConfig.getRegistratorsGroup())) {
            ((TabSheet) getComponentNN("tabSheet")).getTab("stepsTab").setVisible(false);
            ((TabSheet) getComponentNN("tabSheet")).getTab("actionsTab").setVisible(false);
            ((TabSheet) getComponentNN("tabSheet")).getTab("communicationsTab").setVisible(false);
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

        Request request = (Request) getFieldGroup().getDatasource().getItem();
        if (PersistenceHelper.isNew(request)) {
            request.setState(State.Waiting);
        }

        return true;
    }

    public void saveWithPrompt() {
        showOptionDialog(
                "",
                messages.getMainMessage("dialog.saveAndClose.title"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            if (preSave()) {
                                super.save();
                                requestsDs.refresh();
                            }
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }



    public void onSetStep(Component source) {
        showOptionDialog(
                "",
                getMessage("dialog.nextStep.title"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            requestService.newRequestStep(requestsDs.getItem());
                            requestsDs.refresh();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );



    }
}