package com.company.office.service;

import com.company.office.entity.GroupType;
import com.haulmont.cuba.security.entity.User;

import java.util.Date;

public interface ToolsService {
    String NAME = "office_ToolsService";

    User getActiveUser();
    GroupType getActiveGroupType();
    GroupType getGroupType(User user);

    boolean isAdmin();

    long getMoment();
    Date addDaysToNow(Integer days);
    int getCountInt(Integer value);
    double getCountDouble(Integer value);

}