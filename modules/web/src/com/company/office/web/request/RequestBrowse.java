package com.company.office.web.request;

import com.company.office.common.RequestProcessing;
import com.company.office.entity.*;
import com.company.office.common.OfficeTools;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.company.office.web.screens.DialogScreen;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.yarg.formatters.factory.DefaultFormatterFactory;
import com.haulmont.yarg.loaders.factory.DefaultLoaderFactory;
import com.haulmont.yarg.loaders.impl.GroovyDataLoader;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import com.haulmont.yarg.reporting.Reporting;
import com.haulmont.yarg.reporting.RunParams;
import com.haulmont.yarg.structure.Report;
import com.haulmont.yarg.structure.xml.impl.DefaultXmlReader;
import com.haulmont.yarg.util.groovy.DefaultScriptingImpl;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
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
    private CollectionDatasource<RequestStepAction, UUID> actionsDs;

    @Inject
    private GroupTable<Request> table;

    @Inject
    private TabSheet tabSheet;

    @Inject
    private LookupField stepLookup;

    @Override
    public void init(Map<String, Object> params) {
        table.setStyleProvider((request, property) -> {
            if (property == null) {
                int penalty = officeTools.getCountInt(request.getStep().getPenalty());
                if (penalty < 0)
                    return "applicant-penalty";

                if (penalty > 0)
                    return "worker-penalty";
            }
            return null;
        });

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

        editAction.setAfterCommitHandler(e -> {
            focusOnStep();
        });

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
                extraActionsBtn.getAction("reduce").setVisible(officeTools.getCountInt(e.getItem().getStep().getPenalty()) != 0);
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
            stepLookup.setValue(null);
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
            case Others:
                break;
        }
    }

    public Component getDoc(Request request) {
        return new Table.PlainTextCell(request.getInstanceName());
    }

    public Component performedGenerator(RequestStepAction requestStepAction) {
        return officeWeb.getMarkForGenerator(requestStepAction);
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
                    toolsService.blockUser(request.getApplicant());
                    officeWeb.showWarningMessage(this, getMessage("result.cancelled"));
                    break;
                case ARCHIVE_REQUEST:
                    requestProcessing.changeState(request, State.Archived, reason);
                    toolsService.blockUser(request.getApplicant());
                    officeWeb.showWarningMessage(this, getMessage("result.archived"));
                    break;
                default:
            }
            requestsDs.setItem(request);
            getDsContext().commit();
            requestsDs.refresh();
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

    public void onReducePenalty() {
        showOptionDialog(
                "",
                getMessage("extraActionsBtn.reduce") + "?",
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            Request request = requestsDs.getItem();
                            requestProcessing.reducePenalty(request);
                            requestsDs.setItem(request);
                            getDsContext().commit();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    @Inject
    private ExportDisplay exportDisplay;

    public void onPrint() throws Exception {
        String structure = "s:/Delo/CUBA/office/reports/request/request.xml";
        //String output = "d:/temp/request #" + requestsDs.getItem().getInstanceName() + ".pdf";
        String output = "Request #" + requestsDs.getItem().getInstanceName() + ".pdf";

        Report report = new DefaultXmlReader()
                .parseXml(FileUtils.readFileToString(new File(structure), Charset.defaultCharset() ));

        Reporting reporting = new Reporting();
        reporting.setFormatterFactory(new DefaultFormatterFactory());
        reporting.setLoaderFactory(
                new DefaultLoaderFactory()
                        .setGroovyDataLoader(new GroovyDataLoader(new DefaultScriptingImpl())));

        ReportOutputDocument reportOutputDocument = reporting.runReport(
                new RunParams(report).param("Request", requestsDs.getItem())//,
                //new FileOutputStream(output)
        );

        exportDisplay.show(new ByteArrayDataProvider(reportOutputDocument.getContent()), output);
    }
}