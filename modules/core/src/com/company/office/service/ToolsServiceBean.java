package com.company.office.service;

import com.company.office.OfficeConfig;
import com.company.office.entity.GroupType;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(ToolsService.NAME)
public class ToolsServiceBean implements ToolsService {

    @Inject
    private OfficeConfig officeConfig;

    private User getCurrentUser() {
        return AppBeans.get(UserSessionSource.class).getUserSession().getUser();
    }

    @Override
    public User getActiveUser() {
        return getCurrentUser();
    }

    @Override
    public GroupType getActiveGroupType() {
        return getGroupType(getCurrentUser());
    }

    @Override
    public GroupType getGroupType(User user) {
        Map<Group, GroupType> map = new HashMap<>();
        map.put(officeConfig.getRegistratorsGroup(), GroupType.Registrators);
        map.put(officeConfig.getManagersGroup(), GroupType.Managers);
        map.put(officeConfig.getWorkersGroup(), GroupType.Workers);
        map.put(officeConfig.getApplicantsGroup(), GroupType.Applicants);

        if (map.containsKey(user.getGroup())) {
            return map.get(user.getGroup());
        }
        return GroupType.Others;
    }

    @Override
    public boolean isAdmin() {
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
    public long getMoment() {
        return (new Date()).getTime();
    }

}