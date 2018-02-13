package com.company.office.service;


import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;

public interface ToolsService {
    String NAME = "office_ToolsService";

    long unreadLogsCount();
    void blockApplicant(User applicant);
    void commitEntity(Entity entity);
}