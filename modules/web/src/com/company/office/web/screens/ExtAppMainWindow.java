package com.company.office.web.screens;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.UserRole;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.app.mainwindow.AppMainWindow;

import javax.inject.Inject;
import java.util.List;

public class ExtAppMainWindow extends AppMainWindow {

    @Inject
    private UserSession userSession;

    @Override
    public void ready() {
        super.ready();

        List<UserRole> roles = userSession.getUser().getUserRoles();

        boolean superUser = false;
        for (UserRole role: roles) {
            if (role.getRole().getType().equals(RoleType.SUPER)) {
                superUser = true;
                break;
            }
        }

        if (!superUser) {
            openWindow("office$Request.browse", WindowManager.OpenType.NEW_TAB);
        }
    }

}