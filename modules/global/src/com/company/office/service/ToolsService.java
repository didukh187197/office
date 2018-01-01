package com.company.office.service;


import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;

public interface ToolsService {
    String NAME = "office_ToolsService";

    boolean isSuperUser();
    User getUser();
    Group getGroup();
}