package com.company.office.service;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(ToolsService.NAME)
public class ToolsServiceBean implements ToolsService {

    @Override
    public boolean isSuperUser() {
        List<UserRole> roles = AppBeans.get(UserSessionSource.class).getUserSession().getUser().getUserRoles();
        AppBeans.get(UserSessionSource.class).getUserSession().getRoles();

        boolean superUser = false;
        for (UserRole role: roles) {
            if (role.getRole().getType().equals(RoleType.SUPER)) {
                superUser = true;
                break;
            }
        }

        return superUser;
    }
}