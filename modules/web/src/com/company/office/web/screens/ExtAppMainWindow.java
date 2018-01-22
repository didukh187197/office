package com.company.office.web.screens;

import com.haulmont.cuba.gui.WindowManager;
import com.company.office.common.OfficeTools;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;
public class ExtAppMainWindow extends AppMainWindow {

    @Inject
    private OfficeTools officeTools;

    @Override
    public void ready() {
        super.ready();

        /*
        if (!toolsService.isAdmin()) {
            openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
        }
        */
    }

}