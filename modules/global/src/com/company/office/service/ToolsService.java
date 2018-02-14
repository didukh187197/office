package com.company.office.service;


import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;

public interface ToolsService {
    String NAME = "office_ToolsService";

    long unreadLogsCount();
    void blockUser(User applicant);
    void commitEntity(Entity entity);
    int getApplicantPenalty(User applicant);
    int getWorkerPenalty(User worker);
}