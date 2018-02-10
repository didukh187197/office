package com.company.office.web.screensext;

import com.company.office.broadcast.LogCreateEvent;
import com.company.office.broadcast.LogCreateEventBroadcaster;
import com.company.office.common.OfficeCommon;
import com.company.office.common.OfficeTools;
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
    private OfficeTools officeTools;

    @Inject
    private OfficeCommon officeCommon;

    @Inject
    private LogCreateEventBroadcaster broadcaster;

    @Inject
    private BackgroundWorker backgroundWorker;

    @Inject
    private LinkButton eventsBtn;

    private Consumer<LogCreateEvent> messageHandler;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        UIAccessor uiAccessor = backgroundWorker.getUIAccessor();
        messageHandler = event -> uiAccessor.access(() -> {
            if (event.getLog().getRecepient().equals(officeTools.getActiveUser())) {
                showNotification(event.getLog().getInfo(), NotificationType.TRAY);
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
    }

    private void setEventsBtnCaption() {
        eventsBtn.setCaption(getMessage("mainWindow.logsBtn") + officeTools.unreadLogsInfo(officeCommon.unreadLogsCount()));
    }

    public void onEventsBtnClick() {
        LogEvents logs = (LogEvents) openWindow("log-events", WindowManager.OpenType.NEW_TAB);
        logs.addCloseListener(e -> setEventsBtnCaption());
    }

}