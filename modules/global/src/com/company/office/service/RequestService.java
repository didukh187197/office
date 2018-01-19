package com.company.office.service;

import com.company.office.entity.Request;
import com.company.office.entity.RequestLog;
import com.company.office.entity.RequestStep;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;

public interface RequestService {
    String NAME = "office_RequestService";

    RequestStep newStepByPosition(Request request);
    RequestStep newStepByWorker(Request request);
    RequestLog newLogItem(Request request, User recepient, String info, Entity entity);
    void changePositionUserRequestCount(Request request, int count);
}