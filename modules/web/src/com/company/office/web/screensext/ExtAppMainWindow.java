package com.company.office.web.screensext;

import com.company.office.OfficeConfig;
import com.company.office.broadcast.LogsCreatedEvent;
import com.company.office.broadcast.LogsCreatedEventBroadcaster;
import com.company.office.common.OfficeTools;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.company.office.web.requestlog.LogEvents;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.UIAccessor;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Consumer;

public class ExtAppMainWindow extends AppMainWindow {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private ToolsService toolsService;

    @Inject
    private OfficeTools officeTools;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private LogsCreatedEventBroadcaster broadcaster;

    @Inject
    private BackgroundWorker backgroundWorker;

    @Inject
    private LinkButton eventsBtn;

    private Consumer<LogsCreatedEvent> messageHandler;
    private long logsCount;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        UIAccessor uiAccessor = backgroundWorker.getUIAccessor();
        messageHandler = event -> uiAccessor.access(() -> {
            if (logsCount != toolsService.unreadLogsCount()) {
                showNotification(getMessage("mainWindow.checkLogs"), NotificationType.TRAY);
                setEventsBtnCaption();
            }
        });
        broadcaster.subscribe(messageHandler);
    }

    @Override
    public void ready() {
        super.ready();
        setEventsBtnCaption();

        if (!officeTools.isAdmin()) {
            openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
        }

        switch (officeTools.getActiveGroupType()) {
            case Applicants:
                int applicantPenalty = toolsService.getApplicantPenalty(officeTools.getActiveUser());
                if (applicantPenalty != 0) {
                    officeWeb.showWarningMessage(this,
                            String.format(getMessage("mainWindow.warning.penalty"), applicantPenalty, officeConfig.getApplicantPenalty())
                    );
                }
                break;
            case Workers:
                int workerPenalty = toolsService.getWorkerPenalty(officeTools.getActiveUser());
                if (workerPenalty != 0) {
                    officeWeb.showWarningMessage(this,
                            String.format(getMessage("mainWindow.warning.penalty"), workerPenalty, officeConfig.getWorkerPenalty())
                    );
                }
                break;
        }
    }

    private void setEventsBtnCaption() {
        logsCount = toolsService.unreadLogsCount();
        eventsBtn.setCaption(getMessage("mainWindow.logsBtn") + officeTools.unreadLogsInfo(logsCount));
    }

    public void onEventsBtnClick() {
        LogEvents logs = (LogEvents) openWindow("log-events", WindowManager.OpenType.NEW_TAB);
        logs.addCloseListener(e -> setEventsBtnCaption());
    }

}