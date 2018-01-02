package com.company.office.service;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service(ToolsService.NAME)
public class ToolsServiceBean implements ToolsService {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private DataManager dataManager;

    private User getCurrentUser() {
        return AppBeans.get(UserSessionSource.class).getUserSession().getUser();
    }

    private User getUser(UUID userId) {
        LoadContext<User> loadContext = LoadContext.create(User.class).setId(userId).setView("user.browse");
        return dataManager.load(loadContext);
    }

    @Override
    public User getActiveUser() {
        return getCurrentUser();
    }

    @Override
    public Group getActiveGroup() {
        return getCurrentUser().getGroup();
    }

    @Override
    public boolean isActiveSuper() {
        List<UserRole> roles = getCurrentUser().getUserRoles();

        boolean superUser = false;
        for (UserRole role: roles) {
            if (role.getRole().getType().equals(RoleType.SUPER)) {
                superUser = true;
                break;
            }
        }

        return superUser;
    }

    @Override
    public boolean isApplicant(StandardEntity user) {
        User usr = getUser(user.getId());
        return usr.getGroup().equals(officeConfig.getApplicantsGroup());
    }

    @Override
    public boolean isWorker(StandardEntity user) {
        User usr = getUser(user.getId());
        return usr.getGroup().equals(officeConfig.getWorkersGroup());
    }

}