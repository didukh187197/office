package com.company.office.common;

import com.company.office.OfficeConfig;
import com.company.office.entity.*;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.RoleType;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

@Component("office_OfficeTools")
public class OfficeTools {

    @Inject
    private OfficeConfig officeConfig;

    private User getCurrentUser() {
        return AppBeans.get(UserSessionSource.class).getUserSession().getUser();
    }

    public User getActiveUser() {
        return getCurrentUser();
    }

    public GroupType getActiveGroupType() {
        return getGroupType(getCurrentUser());
    }

    public GroupType getGroupType(User user) {
        Group group = user.getGroup();

        if (group.equals(officeConfig.getRegistratorsGroup()))
            return GroupType.Registrators;

        if (group.equals(officeConfig.getManagersGroup()))
            return GroupType.Managers;

        if (group.equals(officeConfig.getWorkersGroup()))
            return GroupType.Workers;

        if (group.equals(officeConfig.getApplicantsGroup()))
            return GroupType.Applicants;

        return GroupType.Others;
    }

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

    public long getMoment() {
        return (new Date()).getTime();
    }

    public Date addDaysToNow(Integer days) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, getCountInt(days));
        return calendar.getTime();
    }

    public int getCountInt(Integer value) {
        return value == null ? 0 : value;
    }

    public double getCountDouble(Integer value) {
        return value == null ? 0 : value;
    }

}
