package com.company.office.web.officeweb;

import com.company.office.entity.Request;
import com.company.office.entity.RequestStep;
import com.company.office.entity.RequestStepAction;
import com.company.office.service.ShedulerService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.App;
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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

@Component("office_OfficeWeb")
public class OfficeWeb {

    @Inject
    private ExportDisplay exportDisplay;

    public void showWarningMessage(Frame frame, String msg) {
        frame.showNotification(msg, Frame.NotificationType.WARNING);
    }

    public void showErrorMessage(Frame frame, String msg) {
        frame.showNotification(msg, Frame.NotificationType.ERROR);
    }

    public void disableContainer(Frame frame, String containerID) {
        com.haulmont.cuba.gui.components.Component.Container container = (com.haulmont.cuba.gui.components.Component.Container) frame.getComponentNN(containerID);
        ComponentsHelper.walkComponents(container, (component, name) -> {
            if (component instanceof FieldGroup) {
                ((FieldGroup) component).setEditable(false);
            } else if (component instanceof Table) {
                ((Table) component).getActions().forEach(action -> action.setEnabled(false));
            } else if (!(component instanceof com.haulmont.cuba.gui.components.Component.Container)) {
                component.setEnabled(false);
            }
        });
    }

    public void showFile(FileDescriptor file) {
        if (file == null)
            return;
        exportDisplay.show(file, ExportFormat.OCTET_STREAM);
    }

    public void showStep(Frame frame, RequestStep step) {
        frame.openEditor("office$RequestStep.edit", step, WindowManager.OpenType.DIALOG);
    }

    public void makeLogsFilter(CollectionDatasource logsDs, LookupField paramsLookup, String parentDs) {
        paramsLookup.setOptionsList(new ArrayList<>(Arrays.asList("History", "Actions", "Communications", "Editing")));
        paramsLookup.setTextInputAllowed(false);
        paramsLookup.addValueChangeListener(e -> {
            String query = String.format("select e from office$RequestLog e where e.request.id = :ds$%s.id ", parentDs);
            if (e.getValue() != null) {
                switch (e.getValue().toString()) {
                    case "History":
                        query += "and (e.attachType = 'com.company.office.entity.Request' or e.attachType = 'com.company.office.entity.RequestStep')";
                        query += String.format("and (e.sender.id = :ds$%s.applicant.id or e.recepient.id = :ds$%s.applicant.id)", parentDs, parentDs);
                        break;
                    case "Actions":
                        query += "and e.attachType = 'com.company.office.entity.RequestStepAction'";
                        break;
                    case "Communications":
                        query += "and e.attachType = 'com.company.office.entity.RequestStepCommunication'";
                        break;
                    case "Editing":
                        query += "and e.attachType is null";
                        break;
                    default: {
                        showErrorMessage(null, "Wrong attach type!");
                        return;
                    }
                }
            }
            query += " order by e.moment";
            logsDs.setQuery(query);
            logsDs.refresh();
        });
    }

    @Inject
    private ComponentsFactory componentsFactory;

    public Label getMarkForGenerator(RequestStepAction requestStepAction) {
        Label lbl = componentsFactory.createComponent(Label.class);
        lbl.setValue("");
        switch (requestStepAction.getType()) {
            case sendFile:
                lbl.setIconFromSet(requestStepAction.getFile() != null ? CubaIcon.CHECK_SQUARE_O : CubaIcon.SQUARE_O);
                break;
            case sendMessage:
                lbl.setIconFromSet(requestStepAction.getMessage() != null ? CubaIcon.CHECK_SQUARE_O : CubaIcon.SQUARE_O);
                break;
        }
        return lbl;
    }

    // Admin methods
    // Used from main menu
    private final String MSG_PACK = "com.company.office.web.officeweb";

    @Inject
    private Messages messages;

    @Inject
    private ShedulerService shedulerService;

    @SuppressWarnings("unused")
    public void checkProcessingDelay() {
        shedulerService.checkProcessingDelay();
        showWarningMessage(App.getInstance().getTopLevelWindow().getFrame(), messages.getMessage(MSG_PACK, "checkRequests"));
    }

    @SuppressWarnings("unused")
    public void setPositionUser() {
        shedulerService.setPositionUser();
        showWarningMessage(App.getInstance().getTopLevelWindow().getFrame(), messages.getMessage(MSG_PACK, "checkRequests"));
    }

    // Reports
    @Inject
    private DataManager dataManager;

    @SuppressWarnings("unused")
    public void requestsList() throws Exception {
        String structure = "s:/Delo/CUBA/office/reports/requests/requests.xml";
        //String output = "d:/temp/requests.xlsx";
        String output = "requests.xlsx";

        LoadContext<Request> loadContext = LoadContext.create(Request.class)
                .setQuery(LoadContext.createQuery("select e from office$Request e order by e.moment"))
                .setView("request-view");

        makeReport(structure, output, "RequestList", dataManager.loadList(loadContext));
    }

    public void makeReport(String structure, String output, String paramName, Object paramValue) throws Exception {
        Report report = new DefaultXmlReader()
                .parseXml(FileUtils.readFileToString(new File(structure), Charset.defaultCharset()));

        Reporting reporting = new Reporting();
        reporting.setFormatterFactory(new DefaultFormatterFactory());
        reporting.setLoaderFactory(
                new DefaultLoaderFactory()
                        .setGroovyDataLoader(new GroovyDataLoader(new DefaultScriptingImpl()))
        );

        ReportOutputDocument reportOutputDocument = reporting.runReport(
                new RunParams(report).param(paramName, paramValue)//,
                //new FileOutputStream(output)
        );

        exportDisplay.show(new ByteArrayDataProvider(reportOutputDocument.getContent()), output);
    }

}
