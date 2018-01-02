package com.company.office.service;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;

public interface ToolsService {
    String NAME = "office_ToolsService";

    User getActiveUser();
    Group getActiveGroup();
    boolean isActiveSuper();

    boolean isApplicant(StandardEntity user);
    boolean isWorker(StandardEntity user);

}