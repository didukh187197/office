package com.company.office.web.screens;

import com.company.office.service.ToolsService;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;
public class ExtAppMainWindow extends AppMainWindow {

    @Inject
    private ToolsService toolsService;

    @Override
    public void ready() {
        super.ready();

        if (!toolsService.isActiveSuper()) {
            openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
        }
    }

}