package com.company.office.service;

import com.company.office.entity.GroupType;
import com.haulmont.cuba.security.entity.User;

public interface ToolsService {
    String NAME = "office_ToolsService";

    User getActiveUser();
    GroupType getActiveGroupType();
    GroupType getGroupType(User user);

    boolean isAdmin();
    long getMoment();

}