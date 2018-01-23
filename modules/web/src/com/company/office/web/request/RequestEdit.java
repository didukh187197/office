package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.web.officeweb.OfficeWeb;
import com.company.office.common.OfficeCommon;
import com.company.office.common.OfficeTools;
import com.company.office.entity.*;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class RequestEdit extends AbstractEditor<Request> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private OfficeCommon officeCommon;

    @Inject
    private OfficeTools officeTools;

    @Inject
    private DataSupplier dataSupplier;

    @Inject
    private FileUploadingAPI fileUploadingAPI;

    @Inject
    private ExportDisplay exportDisplay;

    @Inject
    private FileUploadField uploadField;

    @Inject
    private Button downloadImageBtn;

    @Inject
    private Button clearImageBtn;

    @Inject
    private Datasource<Request> requestDs;

    @Inject
    private Image image;

    @Inject
    private ComponentsFactory componentsFactory;

    @Named("fieldGroupApplicant.applicant")
    private PickerField applicantField;

    @Inject
    private TabSheet tabSheet;

    @Override
    public void init(Map<String, Object> params) {
        addListeners();
    }

    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            getItem().setMoment(officeTools.getMoment());
        }

        tuneComponents();
        setUserInterface();
        showSubmitButton();
        showApproveBtn();
        displayImage();
        updateImageButtons(getItem().getImageFile() != null);
    }

    private void addListeners() {
        Table actionsTable = (Table) getComponentNN("actionsTable");
        EditAction editStepActionAction = (EditAction) actionsTable.getActionNN("edit");
        Datasource<RequestStepAction> actionsDs = getDsContext().getNN("actionsDs");

        editStepActionAction.setBeforeActionPerformedHandler(() -> {
            if (officeTools.getActiveGroupType().equals(GroupType.Applicants)) {
                if (actionsDs.getItem().getApproved() != null) {
                    officeWeb.showWarningMessage(this, getMessage("edit.action.alreadyApproved"));
                    return false;
                }
            }

            editStepActionAction.setWindowParams(ParamsMap.of("request", getItem()));
            return true;
        });

        Table communicationsTable = (Table) getComponentNN("communicationsTable");
        CreateAction createStepCommunicationAction = (CreateAction) communicationsTable.getActionNN("create");
        EditAction editStepCommunicationAction = (EditAction) communicationsTable.getActionNN("edit");

        createStepCommunicationAction.setBeforeActionPerformedHandler(() -> {
            createStepCommunicationAction.setWindowParams(ParamsMap.of("request", getItem()));
            return true;
        });

        editStepCommunicationAction.setBeforeActionPerformedHandler(() -> {
            editStepCommunicationAction.setWindowParams(ParamsMap.of("request", getItem()));
            return true;
        });

        actionsDs.addItemPropertyChangeListener(e -> {
            showSubmitButton();
            showApproveBtn();
        });

        uploadField.addFileUploadSucceedListener(event -> {
            FileDescriptor fd = uploadField.getFileDescriptor();
            try {
                fileUploadingAPI.putFileIntoStorage(uploadField.getFileId(), fd);
            } catch (FileStorageException e) {
                throw new RuntimeException("Error saving file to FileStorage", e);
            }
            getItem().setImageFile(dataSupplier.commit(fd));
            displayImage();
        });

        uploadField.addFileUploadErrorListener(event ->
                showNotification("File upload error", NotificationType.HUMANIZED));

        requestDs.addItemPropertyChangeListener(event -> {
            if ("imageFile".equals(event.getProperty()))
                updateImageButtons(event.getValue() != null);
        });
    }

    private void tuneComponents() {
        applicantField.getLookupAction().setLookupScreenOpenType(WindowManager.OpenType.DIALOG);

        if (officeConfig.getWorkersGroupQuery() != null) {
            ((CollectionDatasource) getDsContext().getNN("workersDs")).setQuery(officeConfig.getWorkersGroupQuery());
        }
    }

    private void setUserInterface() {
        if (officeTools.isAdmin()) {
            ((FieldGroup) getComponentNN("stepParamsFields")).setEditable(true);
            ((FieldGroup) getComponentNN("stepDatesFields")).setEditable(true);
            ((FieldGroup) getComponentNN("stepOtherFields")).setEditable(true);
            return;
        }

        tabSheet.getTab("systemTab").setVisible(false);

        switch (officeTools.getActiveGroupType()) {
            case Registrators:
                tabSheet.setEnabled(false);
                if (!PersistenceHelper.isNew(getItem())) {
                    applicantField.setEditable(false);
                }
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

    private boolean moved = false;

    @Override
    protected boolean preCommit() {

        switch (officeTools.getActiveGroupType()) {
            case Workers:
            case Applicants:
                break;
            default:
                User applicant = getItem().getApplicant();
                if (!officeTools.getGroupType(applicant).equals(GroupType.Applicants))  {
                    officeWeb.showErrorMessage(this, getMessage("warning.notApplicant"));
                    return false;
                }
        }

        Request request = getItem();
        if (PersistenceHelper.isNew(request)) {
            List<RequestLog> logs = new ArrayList<>();
            logs.add(
                    officeCommon.newLogItem(request, request.getApplicant(), getMessage("result.created"), null)
            );
            request.setLogs(logs);

            List<RequestStep> steps = new ArrayList<>();
            request.setSteps(steps);

            if (!moved) {
                officeCommon.moveRequestToNewStepByPosition(request);
                officeCommon.moveRequestToNewStepByWorker(request);
                moved = true;
            }

            setItem(request);

        } else {
            switch (officeTools.getActiveGroupType()) {
                case Workers:
                case Applicants:
                    break;
                default:
                    request.getLogs().add(
                            officeCommon.newLogItem(request, request.getApplicant(), getMessage("result.edited"), null)
                    );
                    if (request.getStep().getUser() != null) {
                        request.getLogs().add(
                                officeCommon.newLogItem(request, request.getStep().getUser(), getMessage("result.edited"), null)
                        );
                    }
                    break;
            }
        }
        return true;
    }

    private void showSubmitButton() {
        getComponentNN("submitBtn").setVisible(false);

        if (!officeTools.getActiveGroupType().equals(GroupType.Applicants))
            return;

        if (getItem().getStep().getState() != State.Waiting)
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

        if (!officeTools.getActiveGroupType().equals(GroupType.Workers))
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

    public void onSubmitBtnClick() {
        showOptionDialog(
                "",
                getMessage("edit.submit"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            Request request = getItem();
                            RequestStep requestStep = request.getStep();
                            requestStep.setSubmitted(new Date());
                            requestStep.setApprovalTerm(officeTools.addDaysToNow(requestStep.getPosition().getDaysForSubmission()));
                            requestStep.setState(State.Approving);
                            request.getLogs().add(
                                    officeCommon.newLogItem(request, requestStep.getUser(), getMessage("result.submitted"), null)
                            );
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onApproveBtnClick() {
        showOptionDialog(
                "",
                getMessage("edit.approve"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            Request request = getItem();
                            RequestStep requestStep = request.getStep();
                            requestStep.setApproved(new Date());
                            request.getLogs().add(
                                    officeCommon.newLogItem(request, request.getApplicant(), getMessage("result.approved"), null)
                            );
                            officeCommon.moveRequestToNewStepByPosition(request);
                            officeCommon.moveRequestToNewStepByWorker(request);
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onDownloadImageBtnClick() {
        if (getItem().getImageFile() != null)
            exportDisplay.show(getItem().getImageFile(), ExportFormat.OCTET_STREAM);
    }

    public void onClearImageBtnClick() {
        getItem().setImageFile(null);
        displayImage();
    }

    private void updateImageButtons(boolean enable) {
        downloadImageBtn.setEnabled(enable);
        clearImageBtn.setEnabled(enable);
    }

    private void displayImage() {
        if (getItem().getImageFile() != null) {
            image.setSource(FileDescriptorResource.class).setFileDescriptor(getItem().getImageFile());
            image.setVisible(true);
        } else {
            image.setVisible(false);
        }
    }
}