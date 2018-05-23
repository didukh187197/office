package com.company.office.web.screensext;

import com.company.office.OfficeConfig;
import com.company.office.broadcast.LogsCreatedEvent;
import com.company.office.broadcast.LogsCreatedEventBroadcaster;
import com.company.office.common.OfficeTools;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.company.office.web.requestlog.LogEvents;
import com.haulmont.cuba.core.global.Messages;
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

    @Inject
    private Messages messages;

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

        String property = emptyProperty();
        if (!property.isEmpty()) {
            mainMenu.getMenuItem("requests").setVisible(false);
            if (officeTools.isAdmin()) {
                openWindow("main-settings-screen", WindowManager.OpenType.DIALOG);
            } else {
                officeWeb.showErrorMessage(this,
                        String.format(getMessage("mainWindow.warning.emptySettings"), property)
                );
            }
        } else {
            if (!officeTools.isAdmin()) {
                openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
            }
        }
    }

    private String emptyProperty() {
        String MSG_PACK = "com.company.office.web.screens";

        String group = messages.getMessage(MSG_PACK, "settingsDialog.type.group");
        if (officeConfig.getManagersGroup() == null) return messages.getMessage(MSG_PACK, "settingsDialog.managers") + group;
        if (officeConfig.getRegistratorsGroup() == null) return messages.getMessage(MSG_PACK, "settingsDialog.registrators") + group;
        if (officeConfig.getApplicantsGroup() == null) return messages.getMessage(MSG_PACK, "settingsDialog.applicants") + group;
        if (officeConfig.getWorkersGroup() == null) return messages.getMessage(MSG_PACK, "settingsDialog.workers") + group;

        String role = messages.getMessage(MSG_PACK, "settingsDialog.type.role");
        if (officeConfig.getManagersRole() == null) return messages.getMessage(MSG_PACK, "settingsDialog.managers") + role;
        if (officeConfig.getRegistratorsRole() == null) return messages.getMessage(MSG_PACK, "settingsDialog.registrators") + role;
        if (officeConfig.getApplicantsRole() == null) return messages.getMessage(MSG_PACK, "settingsDialog.applicants") + role;
        if (officeConfig.getWorkersRole() == null) return messages.getMessage(MSG_PACK, "settingsDialog.workers") + role;

        if (officeConfig.getInitPosition() == null) return messages.getMessage(MSG_PACK, "settingsDialog.initPosition");
        if (officeConfig.getFinalPosition() == null) return messages.getMessage(MSG_PACK, "settingsDialog.finalPosition");

        if (officeConfig.getApplicantPenalty() == null) return messages.getMessage(MSG_PACK, "settingsDialog.applicantPenalty");
        if (officeConfig.getWorkerPenalty() == null) return messages.getMessage(MSG_PACK, "settingsDialog.workerPenalty");

        return "";
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