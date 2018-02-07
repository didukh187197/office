package com.company.office.web.screensext;

import com.company.office.common.OfficeCommon;
import com.company.office.web.requestlog.LogEvents;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.LinkButton;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;

public class ExtAppMainWindow extends AppMainWindow {

    //@Inject
    //private UserSessionSource userSessionSource;

    @Inject
    private OfficeCommon officeCommon;

    @Inject
    private LinkButton eventsBtn;

    @Override
    public void ready() {
        super.ready();
        setEventsBtnCaption();

        /*
        if (!toolsService.isAdmin()) {
            openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
        }
        */
    }

    private void setEventsBtnCaption() {
        eventsBtn.setCaption(getMessage("mainWindow.logsBtn") + officeCommon.getUnreadLogsInfo());
    }

    public void onEventsBtnClick() {
        LogEvents logs = (LogEvents) openWindow("log-events", WindowManager.OpenType.NEW_TAB);
        logs.addCloseListener(e -> setEventsBtnCaption());
    }

}