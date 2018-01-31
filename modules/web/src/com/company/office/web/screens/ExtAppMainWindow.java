package com.company.office.web.screens;

//import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

//import javax.inject.Inject;

public class ExtAppMainWindow extends AppMainWindow {

    //@Inject
    //private UserSessionSource userSessionSource;

    @Override
    public void ready() {
        super.ready();

        //showNotification(userSessionSource.getUserSession().getAttribute("commonGroupId").toString());

        /*
        if (!toolsService.isAdmin()) {
            openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
        }
        */
    }

}