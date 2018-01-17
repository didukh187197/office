package com.company.office.service;

import com.company.office.entity.Request;
import com.company.office.entity.RequestLog;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.security.entity.User;

public interface RequestService {
    String NAME = "office_RequestService";

    Request nextPosition(Request request);
    Request setWorker(Request request);
    RequestLog newLogItem(Request request, User recepient, String info, Entity entity);
}